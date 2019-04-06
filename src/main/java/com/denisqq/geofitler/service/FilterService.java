package com.denisqq.geofitler.service;

import com.denisqq.Logic;
import com.denisqq.functions.LTrapezoid;
import com.denisqq.functions.RTrapezoid;
import com.denisqq.functions.Triangle;
import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.dto.DeviceLocationsRequest;
import com.denisqq.rule.Conclusion;
import com.denisqq.rule.Condition;
import com.denisqq.rule.Rule;
import com.denisqq.rule.Variable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Slf4j
public class FilterService {

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
