package com.denisqq.geofitler.dto;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class DeviceLocationsDto implements Serializable {
  private static final long serialVersionUID = -9045374421056087490L;

  private List<Double> speeds;
  private Double conclusion;
}
