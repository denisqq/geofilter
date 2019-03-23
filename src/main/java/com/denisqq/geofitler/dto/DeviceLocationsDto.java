package com.denisqq.geofitler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DeviceLocationsDto implements Serializable {
  private static final long serialVersionUID = -9045374421056087490L;
  private UUID id;

  private UUID deviceId;

  private UUID employeeId;

  private Date dateTime;

  private Double latitude;

  private Double longitude;

  private Double altitude;

}
