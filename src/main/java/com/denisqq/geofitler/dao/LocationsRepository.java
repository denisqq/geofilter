package com.denisqq.geofitler.dao;



import com.denisqq.geofitler.model.DeviceLocations;

import java.util.List;

public interface LocationsRepository{
  List<DeviceLocations> findAll();
  List<DeviceLocations> getLocations();

  void updateAndSave(List<DeviceLocations> locations);
}
