package com.denisqq.geofitler.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "t_device_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(
  name = "DeviceLocationsMapping",
  entities = {
    @EntityResult(
      entityClass = DeviceLocations.class,
      fields = {
        @FieldResult(name = "id", column = "id"),
        @FieldResult(name = "deviceId", column = "device_id"),
        @FieldResult(name = "employeeId", column = "employee_id"),
        @FieldResult(name = "dateTime", column = "date_time"),
        @FieldResult(name = "latitude", column = "latitude"),
        @FieldResult(name = "longitude", column = "longitude"),
        @FieldResult(name = "altitude", column = "altitude"),
        @FieldResult(name = "deleted", column = "deleted")
      }
    )
  },
  columns = {
    @ColumnResult(name = "speed", type = Double.class),
    @ColumnResult(name = "avgSpeed", type = Double.class),
    @ColumnResult(name = "variance", type = Double.class)
  }
)
@NamedNativeQuery(
  name = "getLocations",
  query = "with dl as (\n" +
    "    select dl.id,\n" +
    "           dl.device_id,\n" +
    "           dl.employee_id,\n" +
    "           dl.longitude,\n" +
    "           dl.latitude,\n" +
    "           dl.altitude,\n" +
    "           dl.date_time,\n" +
    "           dl.deleted\n" +
    "--            rank() over (partition by dl.device_id order by dl.date_time desc)\n" +
    "    from filter.t_device_locations dl\n" +
    "    where dl.deleted = false\n" +
    "),\n" +
    "     locations as (\n" +
    "         select dl.id,\n" +
    "                dl.device_id,\n" +
    "                dl.employee_id,\n" +
    "                dl.longitude,\n" +
    "                dl.latitude,\n" +
    "                dl.altitude,\n" +
    "                dl.date_time,\n" +
    "                dl.deleted,\n" +
    "                earth_distance(ll_to_earth(dl.latitude, dl.longitude),\n" +
    "                               ll_to_earth(dl.latitude2, dl.longitude2)) /\n" +
    "                filter.to_seconds(case\n" +
    "                                      when filter.to_seconds(dl.date_time2 - dl.date_time) = 0\n" +
    "                                          then '1 second'\n" +
    "                                      else\n" +
    "                                          dl.date_time - dl.date_time2 end) * 3.6\n" +
    "                    speed\n" +
    "         from (\n" +
    "                  select *,\n" +
    "                         case\n" +
    "                             when filter.abs(date_trunc('day', lead(dl.date_time)\n" +
    "                                                               over (partition by dl.device_id order by dl.date_time desc)) -\n" +
    "                                             date_trunc('day', dl.date_time)) <\n" +
    "                                  '1 Day'\n" +
    "                                 then lead(dl.longitude, 1, dl.longitude)\n" +
    "                                      over (partition by dl.device_id order by dl.date_time desc)\n" +
    "                             else\n" +
    "                                 dl.longitude\n" +
    "                             end longitude2,\n" +
    "                         case\n" +
    "                             when filter.abs(date_trunc('day', lead(dl.date_time)\n" +
    "                                                               over (partition by dl.device_id order by dl.date_time desc)) -\n" +
    "                                             date_trunc('day', dl.date_time)) <\n" +
    "                                  '1 Day'\n" +
    "                                 then lead(dl.latitude, 1, dl.latitude)\n" +
    "                                      over (partition by dl.device_id order by dl.date_time desc)\n" +
    "                             else\n" +
    "                                 dl.latitude\n" +
    "                             end latitude2,\n" +
    "                         case\n" +
    "                             when filter.abs(date_trunc('day', lead(dl.date_time)\n" +
    "                                                               over (partition by dl.device_id order by dl.date_time desc)) -\n" +
    "                                             date_trunc('day', dl.date_time)) <\n" +
    "                                  '1 Day'\n" +
    "                                 then lead(dl.altitude, 1, dl.altitude)\n" +
    "                                      over (partition by dl.device_id order by dl.date_time desc)\n" +
    "                             else\n" +
    "                                 dl.altitude\n" +
    "                             end altitude2,\n" +
    "                         case\n" +
    "                             when filter.abs(date_trunc('day', lead(dl.date_time)\n" +
    "                                                               over (partition by dl.device_id order by dl.date_time desc)) -\n" +
    "                                             date_trunc('day', dl.date_time)) <\n" +
    "                                  '1 Day'\n" +
    "                                 then lead(dl.date_time, 1, dl.date_time)\n" +
    "                                      over (partition by dl.device_id order by dl.date_time desc)\n" +
    "                             else\n" +
    "                                 dl.date_time\n" +
    "                             end date_time2\n" +
    "                  from dl\n" +
    "--                   where dl.rank <= 3\n" +
    "              ) dl\n" +
    "     )\n" +
    "select l.id,\n" +
    "       l.device_id,\n" +
    "       l.employee_id,\n" +
    "       l.longitude,\n" +
    "       l.latitude,\n" +
    "       l.altitude,\n" +
    "       l.date_time,\n" +
    "       l.deleted,\n" +
    "       l.speed,\n" +
    "       avg(l.speed) over (partition by l.device_id, date_trunc('hour', l.date_time))           as \"avgSpeed\",\n" +
    "       sqrt(var_pop(l.speed) over (partition by l.device_id, date_trunc('hour', l.date_time))) as \"variance\"\n" +
    "from locations l\n" +
    "--where l.speed > 0\n" +
    "order by l.device_id, l.date_time desc\n",
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

  @Column(name = "deleted")
  private Boolean deleted;

  @Transient
  private Double speed;

  @Transient
  private Double avgSpeed;

  @Transient
  private Double variance;
}
