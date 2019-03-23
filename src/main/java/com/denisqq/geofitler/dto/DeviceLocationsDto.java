package com.denisqq.geofitler.dto;

import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class DeviceLocationsDto implements Serializable {
  private static final long serialVersionUID = -9045374421056087490L;
  @Type(type="pg-uuid")
  private UUID id;
  @Type(type="pg-uuid")
  private UUID deviceId;
  @Type(type="pg-uuid")
  private UUID employeeId;

  private Date dateTime;

  private Double latitude;

  private Double longitude;

  private Double altitude;

  private Double speed;
}
