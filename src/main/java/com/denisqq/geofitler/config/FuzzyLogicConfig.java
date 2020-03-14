package com.denisqq.geofitler.config;

import com.denisqq.FuzzyLogic;
import com.denisqq.functions.LTrapezoid;
import com.denisqq.functions.RTrapezoid;
import com.denisqq.functions.Triangle;
import com.denisqq.rule.Conclusion;
import com.denisqq.rule.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static com.denisqq.rule.Condition.singletonCondition;

@Configuration
public class FuzzyLogicConfig {

  @Bean
  public FuzzyLogic speedLogic() {
    Triangle largeMaxSpeed = new Triangle(5.5D, 35.0D, 17.5D);
    Triangle smallMinSpeed = new Triangle(0.0D, 15.0D, 5.D);
    LTrapezoid big = new LTrapezoid(8.0D, 70.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 20.0D);

    return FuzzyLogic.builder()
      .rules(
        Arrays.asList(
          Rule.builder()
            .conditionList(singletonCondition(big, "Большая скорость"))
            .conclusion(Conclusion.createConclusion(largeMaxSpeed, "Большое расстояние", 0.95D))
            .build(),
          Rule.builder()
            .conditionList(singletonCondition(small, "Маленькая скорость"))
            .conclusion(Conclusion.createConclusion(smallMinSpeed, "Большое расстояние", 0.45D))
            .build()
        )
      ).build();

  }


  @Bean
  public FuzzyLogic distanceLogic() {
    Triangle smallMaxDistance = new Triangle(0.0D, 500.0D, 250.D);
    Triangle largeMaxDistance = new Triangle(200D, 1000.0D, 500.0D);
    LTrapezoid big = new LTrapezoid(8.0D, 75.0D);
    RTrapezoid small = new RTrapezoid(0.0D, 15.0D);


    return FuzzyLogic.builder()
      .rules(
        Arrays.asList(
          Rule.builder()
            .conditionList(singletonCondition(big, "Большое расстрояние"))
            .conclusion(Conclusion.createConclusion(largeMaxDistance, "Максимальное расстояние - больше", 0.95D))
            .build(),
          Rule.builder()
            .conditionList(singletonCondition(small, "Маленькая скорость"))
            .conclusion(Conclusion.createConclusion(smallMaxDistance, "Максимальное расстояние - маленькое", 0.45D))
            .build()
        )
      ).build();

  }
}
