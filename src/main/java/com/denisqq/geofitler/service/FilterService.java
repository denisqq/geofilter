package com.denisqq.geofitler.service;

import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.mapper.DeviceMapper;
import com.denisqq.geofitler.repo.LocationsRepository;
import com.denisqq.rule.Rule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;


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

    List<DeviceLocationsDto> ret = deviceMapper.toListDto(repository.findAll());


    log.info("{}: ret={}", DEBUG_STR, ret);
    return ret;
  }

  private List<Rule> initRules() {
    final String DEBUG_STR = "initRules";
    log.info("{}:", DEBUG_STR);

    return null;
  }

}
