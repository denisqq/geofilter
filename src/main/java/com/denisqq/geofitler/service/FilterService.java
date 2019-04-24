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
      IntStream.range(0, vSize)
        .forEach(index -> {
          DeviceLocations location = v.get(index);
          if (location.getSpeed() > (2.5 * location.getVariance()) && location.getVariance() != 0) {
            int skip = limit > index ? Math.abs(index - limit) : 0;
            List<Double> speeds = v.stream()
              .limit(limit)
              .skip(skip)
              .map(DeviceLocations::getSpeed)
              .collect(Collectors.toList());
            double conclusion = logic.calc(speeds);
            double avgConclusion = avgLogic.calc(Collections.singletonList(location.getAvgSpeed()));
            log.info("{}: device={}, conclusion={}, avgConclusion={}", DEBUG_STR, location, conclusion, avgConclusion);
            if (conclusion > avgConclusion) {
              location.setDeleted(true);
              modified.add(location);
            }
          }
        });

    });


    repository.updateAndSave(modified);

  }


  private List<Rule> initRules() {
    final String DEBUG_STR = "initRules";
    log.info("{}:", DEBUG_STR);
    List<Rule> ret = new ArrayList<>();

    Triangle notTeleportation = new Triangle(0.0D, 10.0D, 3.D);
    Triangle teleportation = new Triangle(7.5D, 35.0D, 17.5D);
    LTrapezoid big = new LTrapezoid(7.0D, 35.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 10.0D);

    Rule r1 = new Rule();
    r1.setConditionList(Arrays.asList(
      new Condition(big, "Большая", new Variable(0)),
      new Condition(big, "Большая", new Variable(1)),
      new Condition(big, "Большая", new Variable(2)),
      new Condition(big, "Большая", new Variable(3))
    ));
    r1.setConclusion(
      new Conclusion(notTeleportation, "Оставить", new Variable(0), 1.0D)
    );
    ret.add(r1);

    Rule r2 = new Rule();
    r2.setConditionList(Arrays.asList(
      new Condition(small, "Маленькая", new Variable(0)),
      new Condition(small, "Маленькая", new Variable(1)),
      new Condition(small, "Маленькая", new Variable(2)),
      new Condition(small, "Маленькая", new Variable(3))
    ));
    r2.setConclusion(
      new Conclusion(notTeleportation, "Оставить", new Variable(0), 1D)
    );
    ret.add(r2);

    Rule r3 = new Rule();
    r3.setConditionList(Arrays.asList(
      new Condition(big, "Большая", new Variable(0)),
      new Condition(big, "Большая", new Variable(1)),
      new Condition(small, "Маленькая", new Variable(2)),
      new Condition(small, "Маленькая", new Variable(3))
    ));
    r3.setConclusion(
      new Conclusion(teleportation, "Удалить", new Variable(0), 0.35D)
    );
    ret.add(r3);

    Rule r4 = new Rule();
    r4.setConditionList(Arrays.asList(
      new Condition(small, "Маленькая", new Variable(0)),
      new Condition(small, "Маленькая", new Variable(1)),
      new Condition(big, "Большая", new Variable(2)),
      new Condition(big, "Большая", new Variable(3))
    ));
    r4.setConclusion(
      new Conclusion(teleportation, "Удалить", new Variable(0), 0.35D)
    );
    ret.add(r4);

    Rule r5 = new Rule();
    r5.setConditionList(Arrays.asList(
      new Condition(small, "Маленькая", new Variable(0)),
      new Condition(big, "Большая", new Variable(1)),
      new Condition(big, "Большая", new Variable(2)),
      new Condition(big, "Большая", new Variable(3))
    ));
    r5.setConclusion(
      new Conclusion(notTeleportation, "Оставить", new Variable(0), 0.25D)
    );
    ret.add(r5);

    Rule r6 = new Rule();
    r6.setConditionList(Arrays.asList(
      new Condition(small, "Маленькая", new Variable(0)),
      new Condition(small, "Маленькая", new Variable(1)),
      new Condition(small, "Маленькая", new Variable(2)),
      new Condition(big, "Большая", new Variable(3))
    ));
    r6.setConclusion(
      new Conclusion(notTeleportation, "Оставить", new Variable(0), 0.25D)
    );
    ret.add(r6);

    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }


  private List<Rule> avgRules() {

    List<Rule> ret = new ArrayList<>();
    Triangle notTeleportation = new Triangle(0.0D, 10.0D, 3.D);
    Triangle teleportation = new Triangle(7.5D, 35.0D, 17.5D);
    LTrapezoid big = new LTrapezoid(7.0D, 35.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 10.0D);

    Rule r1 = new Rule();
    r1.setConditionList(Collections.singletonList(
      new Condition(big, "Большая", new Variable(0))
    ));
    r1.setConclusion(
      new Conclusion(teleportation, "удалить", new Variable(0), 1.0D)
    );

    Rule r2 = new Rule();
    r2.setConditionList(Collections.singletonList(
      new Condition(small, "Маленкая", new Variable(0))
    ));
    r2.setConclusion(
      new Conclusion(notTeleportation, "Оставить", new Variable(0), 0.25D)
    );
    ret.add(r2);

    return ret;
  }
}
