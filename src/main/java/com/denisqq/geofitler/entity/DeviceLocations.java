package com.denisqq.geofitler.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "t_device_locations")
@Getter
@Setter
@NoArgsConstructor
public class DeviceLocations implements Serializable {
  private static final long serialVersionUID = 8158203280808418739L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "device_id")
  private UUID deviceId;

  @Column(name = "employee_id")
  private UUID employeeId;

  @Column(name = "date_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dateTime;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "altitude")
  private Double altitude;

}
