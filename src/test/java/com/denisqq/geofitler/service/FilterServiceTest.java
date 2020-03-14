package com.denisqq.geofitler.service;

import com.denisqq.geofitler.dao.LocationsRepository;
import com.denisqq.geofitler.model.DeviceLocations;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.denisqq.geofitler.TestUtils.distanceLogic;
import static com.denisqq.geofitler.TestUtils.speedLogic;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class FilterServiceTest {


  @Mock
  private LocationsRepository repository;

  @InjectMocks
  private FilterService filterService = new FilterService(repository, speedLogic(), distanceLogic());


  private List<DeviceLocations> deviceLocations = new ArrayList<>();


  @Before
  public void setUp() throws Exception {
    double[][] points = new double[][]{
      {59.27623390000000, 56.98569960000000},
      {59.28362200000000, 57.00128880000000},
      {59.28346530000000, 57.00251230000000},
      {59.28340950000000, 57.00312810000000},
      {59.27623390000000, 56.98569960000000},
      {59.28535470000000, 56.98780720000000},
      {59.27623390000000, 56.98569960000000},
      {59.28269640000000, 57.00235120000000},
      {59.28303820000000, 57.00345230000000},
      {59.28382000000000, 56.98337390000000},
      {59.27623390000000, 56.98569960000000},
      {59.28535470000000, 56.98780720000000},
      {59.28535470000000, 56.98780720000000},
      {59.28535470000000, 56.98780720000000},
      {59.28306020000000, 57.00327340000000},
      {59.28312550000000, 57.00377570000000},
      {59.28539800000000, 56.99498070000000},
      {59.28629020000000, 56.98885730000000},
      {59.28308910000000, 57.00342460000000},
      {59.28296710000000, 57.00328920000000}
    };
    String[] dates = new String[]{
      "2019-04-24 06:11:35",
      "2019-04-24 06:22:31",
      "2019-04-24 06:21:23",
      "2019-04-24 06:00:40",
      "2019-04-24 06:08:27",
      "2019-04-24 06:17:00",
      "2019-04-24 06:18:06",
      "2019-04-24 06:05:05",
      "2019-04-24 06:19:13",
      "2019-04-24 06:25:50",
      "2019-04-24 06:01:45",
      "2019-04-24 06:09:29",
      "2019-04-24 06:02:51",
      "2019-04-24 06:06:14",
      "2019-04-24 06:03:56",
      "2019-04-24 06:10:32",
      "2019-04-24 06:23:35",
      "2019-04-24 06:24:42",
      "2019-04-24 06:07:26",
      "2019-04-24 06:12:40"
    };
    double[] altitude = new double[]{0.00000000000000,
      188.8002929687500,
      203.4393920898437,
      204.2170410156250,
      0.00000000000000,
      0.00000000000000,
      0.00000000000000,
      168.9955444335937,
      199.6892700195312,
      208.1358642578125,
      0.00000000000000,
      0.00000000000000,
      0.00000000000000,
      0.00000000000000,
      205.9059448242187,
      193.3776855468750,
      173.2705078125000,
      193.3250122070312,
      198.3425292968750,
      171.64947509765625};

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    for (int i = 0; i < 19; i++) {
      DeviceLocations deviceLocation = DeviceLocations.builder()
        .deviceId(UUID.fromString("11ab77eb-4e43-419c-81c1-55a3ee483d16"))
        .build();
      deviceLocation.setLongitude(points[i][0]);
      deviceLocation.setLatitude(points[i][1]);
      deviceLocation.setAltitude(altitude[i]);
      deviceLocation.setDateTime(LocalDateTime.parse(dates[i], formatter));
      deviceLocations.add(deviceLocation);

    }
  }

  @Test
  public void filterLocations() {

    List<DeviceLocations> deviceLocationsList = filterService.filterLocations(deviceLocations);

    deviceLocationsList.forEach(location -> {
      log.info("{};{};{};{}", location.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), location.getLongitude(), location.getLatitude(), location.getAltitude());
    });

  }
}
