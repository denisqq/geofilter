package com.denisqq.geofitler.schedule;


import com.denisqq.geofitler.dao.LocationsRepository;
import com.denisqq.geofitler.model.DeviceLocations;
import com.denisqq.geofitler.service.FilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class Schedule {

  @Autowired
  private LocationsRepository repository;
  @Autowired
  private FilterService filter;

  @Scheduled(fixedRate = 10000)
  public void findGrownUp() {
    final String DEBUG_STR = "findGrownUp";
    log.info("{}: time={}", DEBUG_STR, LocalDateTime.now());

    List<DeviceLocations> deviceLocations = repository.getLocations();

    filter.filterLocations(deviceLocations);
    log.info("{}: ret={}", DEBUG_STR, deviceLocations);
  }

}
