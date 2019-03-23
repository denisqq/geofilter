package com.denisqq.geofitler.entity;

import com.denisqq.geofitler.dto.DeviceLocationsDto;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "t_device_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(
  name = "DeviceLocationsMapping",
  classes = {
    @ConstructorResult(
      targetClass = DeviceLocationsDto.class,
      columns = {
        @ColumnResult(name = "id", type = UUID.class),
        @ColumnResult(name = "deviceId", type = UUID.class),
        @ColumnResult(name = "employeeId", type = UUID.class),
        @ColumnResult(name = "dateTime", type = Date.class),
        @ColumnResult(name = "latitude", type = Double.class),
        @ColumnResult(name = "longitude", type = Double.class),
        @ColumnResult(name = "altitude", type = Double.class),
        @ColumnResult(name = "speed", type = Double.class),
      }
    )
  }
)
@NamedNativeQuery(
  name = "DeviceLocations.deviceLocationsQuery",
  query = "with dl as (\n" +
    "    select dl.id,\n" +
    "           dl.device_id,\n" +
    "           dl.employee_id,\n" +
    "           dl.longitude,\n" +
    "           dl.latitude,\n" +
    "           dl.altitude,\n" +
    "           dl.date_time,\n" +
    "           rank() over (partition by dl.device_id order by dl.date_time desc)\n" +
    "    from filter.t_device_locations dl\n" +
    "  ),\n" +
    "       locations as (\n" +
    "         select dl.id,\n" +
    "                dl.device_id as \"deviceId\",\n" +
    "                dl.employee_id as \"employeeId\",\n" +
    "                dl.longitude,\n" +
    "                dl.latitude,\n" +
    "                dl.altitude,\n" +
    "                dl.date_time as \"dateTime\",\n" +
    "                cast((1609.34 * 2 * 3961 * asin(sqrt((sin(radians((dl.latitude2 - dl.latitude) / 2))) ^ 2 +\n" +
    "                                                cos(radians(dl.latitude)) * cos(radians(dl.latitude2)) *\n" +
    "                                                (sin(radians((dl.longitude2 - dl.longitude) / 2))) ^ 2))) / (abs(\n" +
    "                    extract(seconds from (case\n" +
    "                                            when extract(seconds from dl.date_time2 - dl.date_time) = 0 then '1 second'\n" +
    "                                            else dl.date_time2 - dl.date_time end)))) as double precision)  as \"speed\"\n" +
    "         from (\n" +
    "                select *,\n" +
    "                       coalesce(\n" +
    "                           lead(dl.longitude) over (partition by dl.device_id order by dl.date_time desc),\n" +
    "                           dl.longitude\n" +
    "                         ) longitude2,\n" +
    "                       coalesce(\n" +
    "                           lead(dl.latitude) over (partition by dl.device_id order by dl.date_time desc),\n" +
    "                           dl.latitude\n" +
    "                         ) latitude2,\n" +
    "                       coalesce(\n" +
    "                           lead(dl.altitude) over (partition by dl.device_id order by dl.date_time desc),\n" +
    "                           dl.altitude\n" +
    "                         ) altitude2,\n" +
    "                       coalesce(\n" +
    "                           lead(dl.date_time) over (partition by dl.device_id order by dl.date_time desc),\n" +
    "                           dl.date_time\n" +
    "                         ) date_time2\n" +
    "                from dl\n" +
    "                where dl.rank <= 3\n" +
    "              ) dl\n" +
    "       )\n" +
    "  select *\n" +
    "  from locations l",
  resultSetMapping = "DeviceLocationsMapping"
)
public class DeviceLocations implements Serializable {
  private static final long serialVersionUID = 8158203280808418739L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
    name = "UUID",
    strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  @Type(type="pg-uuid")
  private UUID id;

  @Column(name = "device_id")
  @Type(type="pg-uuid")
  private UUID deviceId;

  @Column(name = "employee_id")
  @Type(type="pg-uuid")
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

  @Transient
  private Double speed;

}
