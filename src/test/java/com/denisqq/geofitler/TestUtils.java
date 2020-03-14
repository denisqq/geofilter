package com.denisqq.geofitler;

import com.denisqq.FuzzyLogic;
import com.denisqq.functions.LTrapezoid;
import com.denisqq.functions.RTrapezoid;
import com.denisqq.functions.Triangle;
import com.denisqq.rule.Conclusion;
import com.denisqq.rule.Rule;

import java.util.Arrays;

import static com.denisqq.rule.Condition.singletonCondition;

public class TestUtils {

  public static FuzzyLogic speedLogic() {
    Triangle largeMaxSpeed = new Triangle(5.5D, 35.0D, 17.5D);
    Triangle smallMinSpeed = new Triangle(0.0D, 20.0D, 15.D);
    LTrapezoid big = new LTrapezoid(8.0D, 70.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 12.0D);

    return FuzzyLogic.builder()
      .rules(
        Arrays.asList(
          Rule.builder()
            .conditionList(singletonCondition(big, "Большая скорость"))
            .conclusion(Conclusion.createConclusion(largeMaxSpeed, "Максимальная скорость большая", 0.45D))
            .build(),
          Rule.builder()
            .conditionList(singletonCondition(small, "Маленькая скорость"))
            .conclusion(Conclusion.createConclusion(smallMinSpeed, "Маленькая максимальная скорость", 0.15D))
            .build()
        )
      ).build();

  }

  public static FuzzyLogic distanceLogic() {
    Triangle smallMaxDistance = new Triangle(0.0D, 800.0D, 500.D);
    Triangle largeMaxDistance = new Triangle(100D, 2000.0D, 1000.0D);
    LTrapezoid big = new LTrapezoid(7.0D, 75.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 12.0D);


    return FuzzyLogic.builder()
      .rules(
        Arrays.asList(
          Rule.builder()
            .conditionList(singletonCondition(big, "Большое расстрояние"))
            .conclusion(Conclusion.createConclusion(largeMaxDistance, "Максимальное расстояние - больше", 0.45D))
            .build(),
          Rule.builder()
            .conditionList(singletonCondition(small, "Маленькая скорость"))
            .conclusion(Conclusion.createConclusion(smallMaxDistance, "Максимальное расстояние - маленькое", 0.15))
            .build()
        )
      ).build();

  }
}
