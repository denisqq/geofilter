package com.denisqq.geofitler.service;

import com.denisqq.Logic;
import com.denisqq.functions.LTrapezoid;
import com.denisqq.functions.RTrapezoid;
import com.denisqq.functions.Triangle;
import com.denisqq.geofitler.dao.LocationsRepository;
import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.dto.DeviceLocationsRequest;
import com.denisqq.geofitler.model.DeviceLocations;
import com.denisqq.rule.Conclusion;
import com.denisqq.rule.Condition;
import com.denisqq.rule.Rule;
import com.denisqq.rule.Variable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class FilterService {

  @Autowired
  private LocationsRepository repository;

  public DeviceLocationsDto filterLocations(final DeviceLocationsRequest request) {
    final String DEBUG_STR = "getLocations";
    log.info("{}:", DEBUG_STR);

    DeviceLocationsDto ret = new DeviceLocationsDto();
    List<Rule> rules = initRules();
    Logic logic = new Logic();
    logic.setRules(rules);
    ret.setSpeeds(request.getSpeeds());
    ret.setConclusion(logic.calc(request.getSpeeds()));

    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }


  public void filterLocations(final List<DeviceLocations> locations) {
    final String DEBUG_STR = "filterLocations";
    log.info("{}: locations={}", DEBUG_STR, locations);


    List<Rule> rules = initRules();
    Logic logic = new Logic();
    logic.setRules(rules);
    Logic avgLogic = new Logic();
    avgLogic.setRules(avgRules());

    Map<UUID, List<DeviceLocations>> mapLoc = locations.stream().collect(Collectors.groupingBy(DeviceLocations::getDeviceId));
    List<DeviceLocations> modified = new ArrayList<>();
    int limit = 4;
    mapLoc.forEach((k, v) -> {
      int vSize = v.size();


      IntStream.range(0, vSize - 1)
        .forEach(index -> {
          DeviceLocations location = locations.get(index);
          double vMax = avgLogic.calc(Collections.singletonList(location.getAvgSpeed())) * location.getAvgSpeed();
          double dRMax = logic.calc(Collections.singletonList(location.getAvgSpeed()));
          if ((location.getSpeed() - v.get(index + 1).getSpeed()) > vMax) {
            if(index < vSize - 5) {
              IntStream.range(index + 1, index + 5).forEach(j -> {
                DeviceLocations dl = v.get(j);
                double distance = distance(dl.getLatitude(), location.getLatitude(), dl.getLongitude(), location.getLongitude());
                if(distance < location.getDistance() * dRMax) {
//                  modified.addAll(v.stream().skip(index).limit(j).peek(x -> x.setDeleted(true)).collect(Collectors.toList()));

                  location.setDeleted(true);
                  modified.add(location);
                }
              });
            }
          }

        });


    });


    repository.updateAndSave(modified);

    log.info("{}: endTime={}", DEBUG_STR, LocalDateTime.now());

  }


  private List<Rule> initRules() {
    final String DEBUG_STR = "initRules";
    log.info("{}:", DEBUG_STR);
    Triangle vMaxSmall = new Triangle(0.0D, 500.0D, 250.D);
    Triangle vMaxLarge = new Triangle(200D, 1000.0D, 500.0D);
    LTrapezoid big = new LTrapezoid(8.0D, 75.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 15.0D);

    List<Rule> ret = new ArrayList<>();

    Rule r1 = new Rule();
    r1.setConditionList(Collections.singletonList(
      new Condition(big, "Большая", new Variable(0))
    ));
    r1.setConclusion(
      new Conclusion(vMaxLarge, "Максимальное расстояние большое", new Variable(0), 0.95D)
    );
    ret.add(r1);

    Rule r2 = new Rule();
    r2.setConditionList(Collections.singletonList(
      new Condition(small, "Маленкая", new Variable(0))
    ));
    r2.setConclusion(
      new Conclusion(vMaxSmall, "Максимальное расстояние малое", new Variable(0), 0.45D)
    );
    ret.add(r2);


    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }


  private List<Rule> avgRules() {
    final String DEBUG_STR = "avgRules";
    log.info("{}:", DEBUG_STR);


    Triangle vMaxSmall = new Triangle(0.0D, 15.0D, 5.D);
    Triangle vMaxLarge = new Triangle(5.5D, 35.0D, 17.5D);
    LTrapezoid big = new LTrapezoid(8.0D, 70.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 20.0D);

    List<Rule> ret = new ArrayList<>();

    Rule r1 = new Rule();
    r1.setConditionList(Collections.singletonList(
      new Condition(big, "Большая", new Variable(0))
    ));
    r1.setConclusion(
      new Conclusion(vMaxLarge, "Максимальная скорость большая", new Variable(0), 0.95D)
    );
    ret.add(r1);

    Rule r2 = new Rule();
    r2.setConditionList(Collections.singletonList(
      new Condition(small, "Маленкая", new Variable(0))
    ));
    r2.setConclusion(
      new Conclusion(vMaxSmall, "Максимальная скорсоть маленькая", new Variable(0), 0.45D)
    );
    ret.add(r2);


    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }


  public static double distance(double lat1, double lat2, double lon1,
                                double lon2) {

    final int R = 6371; // Radius of the earth

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
      + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
      * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000; // convert to meters

    distance = Math.pow(distance, 2);

    return Math.sqrt(distance);
  }
}
