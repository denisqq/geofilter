package com.denisqq.geofitler.repo;

import com.denisqq.geofitler.entity.DeviceLocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface LocationsRepository extends JpaRepository<DeviceLocations, UUID> {
  List<DeviceLocations> findAll();
}
