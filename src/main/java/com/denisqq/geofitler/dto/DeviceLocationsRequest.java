package com.denisqq.geofitler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class DeviceLocationsRequest implements Serializable {
  private static final long serialVersionUID = 7248502331449031505L;

  private List<Double> speeds;
}
