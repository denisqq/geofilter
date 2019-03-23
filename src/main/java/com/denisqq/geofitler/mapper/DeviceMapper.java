package com.denisqq.geofitler.mapper;

import com.denisqq.geofitler.dto.DeviceLocationsDto;
import com.denisqq.geofitler.entity.DeviceLocations;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
  injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DeviceMapper {

  DeviceLocationsDto toDto(DeviceLocations deviceLocations );
  List<DeviceLocationsDto> toListDto(List<DeviceLocations> deviceLocationsList);
}
