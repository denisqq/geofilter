package com.denisqq.geofitler.service;

import com.denisqq.FuzzyLogic;
import com.denisqq.functions.LTrapezoid;
import com.denisqq.functions.RTrapezoid;
import com.denisqq.functions.Triangle;
import com.denisqq.geofitler.dao.LocationsRepository;
import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.dto.DeviceLocationsRequest;
import com.denisqq.geofitler.model.DeviceLocations;
import com.denisqq.rule.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class FilterService {

  private final LocationsRepository repository;
  private final FuzzyLogic speedLogic;
  private final FuzzyLogic distanceLogic;

  public FilterService(LocationsRepository repository, FuzzyLogic speedLogic, FuzzyLogic distanceLogic) {
    this.repository = repository;
    this.speedLogic = speedLogic;
    this.distanceLogic = distanceLogic;
  }

  public DeviceLocationsDto filterLocations(final DeviceLocationsRequest request) {
    final String DEBUG_STR = "getLocations";
    log.info("{}:", DEBUG_STR);

    DeviceLocationsDto ret = new DeviceLocationsDto();
//    List<Rule> rules = initRules();
//    Logic logic = new Logic();
//    logic.setRules(rules);
//    ret.setSpeeds(request.getSpeeds());
//    ret.setConclusion(logic.calc(request.getSpeeds()));

    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }


  public List<DeviceLocations> filterLocations(final List<DeviceLocations> locations) {
    final String DEBUG_STR = "filterLocations";
    log.info("{}: locations={}", DEBUG_STR, locations);

    Map<UUID, List<DeviceLocations>> deviceLocationsMap = locations.stream().collect(Collectors.groupingBy(DeviceLocations::getDeviceId));

    List<DeviceLocations> ret = new ArrayList<>();

    Set<DeviceLocations> toDelete = new HashSet<>();
    deviceLocationsMap.forEach((k, v) -> {

      List<DeviceLocations> deviceLocationsList = v.stream().sorted(Comparator.comparing(DeviceLocations::getDateTime))
        .collect(Collectors.toUnmodifiableList());

      calculateSpeed(deviceLocationsList.listIterator());
      double avgSpeed = deviceLocationsList.stream().mapToDouble(DeviceLocations::getSpeed).average().getAsDouble();
      double avgDistance = deviceLocationsList.stream().mapToDouble(DeviceLocations::getDistance).average().getAsDouble();

      log.debug("{} avgSpeed={}", k, avgSpeed);
      double maxAvgSpeed = speedLogic.calc(Collections.singletonList(avgSpeed)) * avgSpeed;
      double maxAvgDistance = distanceLogic.calc(Collections.singletonList(avgSpeed)) * avgDistance;


      final int length = deviceLocationsList.size();

      int i = 0;
      while (i < length) {
        DeviceLocations current = deviceLocationsList.get(i);
        final int nextIndex = i + 1;
        if (nextIndex != length) {
          DeviceLocations next = deviceLocationsList.get(nextIndex);

          final double speedDifference = Math.abs(current.getSpeed() - next.getSpeed());
          if (speedDifference > maxAvgSpeed) {
            double closestDistance = next.getDistance();
            int closestLocationIndex = nextIndex;
            for (int j = nextIndex; j < length; j++) {
              DeviceLocations deviceLocations = deviceLocationsList.get(j);
              double distance = distance(current.getLatitude(), deviceLocations.getLatitude(), current.getLongitude(), deviceLocations.getLongitude());
              if (distance < closestDistance) {
                closestDistance = distance;
                closestLocationIndex = j;
              }
            }
            if (closestDistance < maxAvgDistance) {
              toDelete.addAll(deviceLocationsList.subList(nextIndex, closestLocationIndex));
              i = closestLocationIndex;
            } else {
              i++;
            }
          } else {
            i++;
          }
          if (!toDelete.contains(current)) {
            ret.add(current);
          }
        } else {
          ret.add(current);
          i++;
        }
      }
      toDelete.forEach(deviceLocations -> deviceLocations.setDeleted(true));

    });


    log.info("{}: endTime={}", DEBUG_STR, LocalDateTime.now());


    return ret;
  }

  private static void calculateSpeed(ListIterator<DeviceLocations> deviceLocationsIterator) {
    DeviceLocations prevDeviceLocation = null;
    while (deviceLocationsIterator.hasNext()) {
      double speed = 0.0D;
      double distance = 0.0D;
      DeviceLocations currentDeviceLocation = deviceLocationsIterator.next();
      if (prevDeviceLocation != null) {
        distance = distance(currentDeviceLocation.getLatitude(), prevDeviceLocation.getLatitude(), currentDeviceLocation.getLongitude(), prevDeviceLocation.getLongitude()); //Метры
        long secondsBetweenLocation = Duration.between(prevDeviceLocation.getDateTime(), currentDeviceLocation.getDateTime()).getSeconds(); //Секунды
        speed = calculateSpeed(distance, secondsBetweenLocation); // КМ\Ч

      }
      currentDeviceLocation.setDistance(distance);
      currentDeviceLocation.setSpeed(speed);
      prevDeviceLocation = currentDeviceLocation;
    }
  }


  private static double calculateSpeedBetweenLocations(DeviceLocations d1, DeviceLocations d2) {
    double distance = distance(d1.getLatitude(), d2.getLatitude(), d1.getLongitude(), d2.getLongitude()); //Метры
    long secondsBetweenLocation = Duration.between(d2.getDateTime(), d1.getDateTime()).getSeconds(); //Секунды
    return calculateSpeed(distance, secondsBetweenLocation);
  }

  private static double calculateSpeed(double distance, long seconds) {
    return distance / seconds * 3.6D;
  }

  private static double distance(double lat1, double lat2, double lon1,
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
