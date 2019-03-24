package com.denisqq.geofitler.service;

import com.denisqq.Logic;
import com.denisqq.functions.LTrapezoid;
import com.denisqq.functions.RTrapezoid;
import com.denisqq.functions.Triangle;
import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.mapper.DeviceMapper;
import com.denisqq.geofitler.repo.LocationsRepository;
import com.denisqq.rule.Conclusion;
import com.denisqq.rule.Condition;
import com.denisqq.rule.Rule;
import com.denisqq.rule.Variable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;


@Component
@Slf4j
public class FilterService {

  @Autowired
  private LocationsRepository repository;

  @Autowired
  private DeviceMapper deviceMapper;

  public List<DeviceLocationsDto> getLocations() {
    final String DEBUG_STR = "getLocations";
    log.info("{}:", DEBUG_STR);

    List<DeviceLocationsDto> ret = repository.deviceLocationsQuery();
    List<Rule> rules = initRules();
    Logic logic = new Logic();
    logic.setRules(rules);
    Map<UUID, List<Double>> speeds = ret.stream()
      .collect(groupingBy(DeviceLocationsDto::getDeviceId, Collectors.mapping(DeviceLocationsDto::getSpeed, Collectors.toList())));
    log.info("{}: speeds={}", DEBUG_STR, speeds);
    Map<UUID, Double> avgSpeed = ret.stream()
      .collect(groupingBy(DeviceLocationsDto::getDeviceId, averagingDouble(DeviceLocationsDto::getSpeed)));
    log.info("{}: avgSpeed={}", DEBUG_STR, avgSpeed);
    speeds.forEach((key, value) -> {
      Double fuzzyConclusion = logic.calc(value);
      log.info("{}: fuzzyConclusion={}", key, fuzzyConclusion);
      Double avgFuzzyConclusion = logic.calc(Arrays.asList(avgSpeed.get(key), 0D));
      log.info("{}: avgFuzzyConclusion={}", key, avgFuzzyConclusion);
      ret.forEach(loc -> {
        if(loc.getDeviceId().equals(key)) {
          loc.setOnCar(avgFuzzyConclusion < fuzzyConclusion);
        }
      });
    });

    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }

  private List<Rule> initRules() {
    final String DEBUG_STR = "initRules";
    log.info("{}:", DEBUG_STR);
    List<Rule> ret = new ArrayList<>();

    Triangle onFoot = new Triangle(0.0D, 3.0D, 1.5D);
    Triangle onCar = new Triangle(3.0D, 15.0D, 8.0D);
    LTrapezoid big = new LTrapezoid(2.0D, 8.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 3.0D);

    Rule r1 = new Rule();
    r1.setConditionList(Arrays.asList(
      new Condition(big, "Большая", new Variable(0)),
      new Condition(big, "Большая", new Variable(1))
    ));
    r1.setConclusion(
      new Conclusion(onCar, "На машине", new Variable(0), 1.0D)
    );
    ret.add(r1);

    Rule r2 = new Rule();
    r2.setConditionList(Arrays.asList(
      new Condition(small, "Маленькая", new Variable(0)),
      new Condition(small, "Маленькая", new Variable(1))
    ));
    r2.setConclusion(
      new Conclusion(onFoot, "Пешком", new Variable(0), 1.0D)
    );
    ret.add(r2);

    Rule r3 = new Rule();
    r3.setConditionList(Arrays.asList(
      new Condition(big, "Большая", new Variable(0)),
      new Condition(small, "Маленькая", new Variable(1))
    ));
    r3.setConclusion(
      new Conclusion(onFoot, "Пешком", new Variable(0), 0.5D)
    );
    ret.add(r3);

    Rule r4 = new Rule();
    r4.setConditionList(Arrays.asList(
      new Condition(small, "Маленькая", new Variable(0)),
      new Condition(big, "Большая", new Variable(1))
    ));
    r4.setConclusion(
      new Conclusion(onCar, "На машине", new Variable(0), 0.5D)
    );
    ret.add(r3);

    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }

}
