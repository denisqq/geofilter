package com.denisqq.geofitler.dao.impl;

import com.denisqq.geofitler.dao.LocationsRepository;
import com.denisqq.geofitler.model.DeviceLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LocationsRepositoryImpl implements LocationsRepository {

  @Autowired
  EntityManager em;

  private static DeviceLocations apply(Object[] x) {
    DeviceLocations dev = (DeviceLocations) x[0];
    dev.setSpeed((Double) x[1]);
    dev.setAvgSpeed((Double) x[2]);
    dev.setVariance((Double) x[3]);

    return dev;
  }


  @Override
  public List<DeviceLocations> findAll() {
    return null;
  }

  @Override
  @Transactional
  @SuppressWarnings("unchecked")
  public List<DeviceLocations> getLocations() {

    List<Object[]> objects =  em.createNamedQuery("getLocations").getResultList();

    return objects.stream().map(LocationsRepositoryImpl::apply).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateAndSave(List<DeviceLocations> locations) {

    locations.forEach(x -> {
      em.merge(x);
    });

    em.flush();
  }


}
