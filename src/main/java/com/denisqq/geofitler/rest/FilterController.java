package com.denisqq.geofitler.rest;

import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller

public class FilterController {

  @Autowired
  private FilterService filters;

  @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DeviceLocationsDto>> filter() {
    return new ResponseEntity<>(filters.getLocations(), HttpStatus.OK);
  }

}
