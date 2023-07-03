package inc.always.right.temp.anomalydetector.temperature.detector;

import com.jupitertools.springtestredis.RedisTestContainer;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest/*(properties = { "anomaly.detector.strategy=timestamp" })*/
@RedisTestContainer(
        hostTargetProperty = "spring.data.redis.host",
        portTargetProperty = "spring.data.redis.port"
)
@Testcontainers
class AverageAnomalyDetectorStrategyIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgresql = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testcontainer")
            .withUsername("user")
            .withPassword("pass");

    @Autowired
    AnomalyDetectorStrategy strategy;
    @Autowired
    RecentTemperatureMeasurementRepository recentTemperatureMeasurementRepository;

    @Test
    void givenAnomaly_shouldDetectAndSave() {
        List<String> temperatures = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        String thermometerId = UUID.randomUUID().toString();
        String roomId = UUID.randomUUID().toString();

        temperatures.forEach(
                temperature -> {
                    recentTemperatureMeasurementRepository.save(new RecentTemperatureMeasurement(
                            null,
                            thermometerId,
                            roomId,
                            new BigDecimal(temperature),
                            now()
                    ));
                }
        );

        TemperatureMeasurement measurement = new TemperatureMeasurement(
                UUID.fromString(thermometerId), UUID.fromString(roomId), new BigDecimal("27.1"), null
        );

        //when
        DetectorResult result = strategy.findAnomalies(measurement);

        //then
        assertEquals(true, result.foundAnomaly());
    }

    @Test
    void givenNotAnomaly_shouldNotDetect() {
        List<String> temperatures = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        String thermometerId = UUID.randomUUID().toString();
        String roomId = UUID.randomUUID().toString();

        temperatures.forEach(
                temperature -> {
                    recentTemperatureMeasurementRepository.save(new RecentTemperatureMeasurement(
                            null,
                            thermometerId,
                            roomId,
                            new BigDecimal(temperature),
                            now()
                    ));
                }
        );

        TemperatureMeasurement measurement = new TemperatureMeasurement(
                UUID.fromString(thermometerId), UUID.fromString(roomId), new BigDecimal("23.1"), null
        );

        //when
        DetectorResult result = strategy.findAnomalies(measurement);

        //then
        assertEquals(false, result.foundAnomaly());
    }

    @AfterAll
    static void teardown() {
        postgresql.stop();
    }
}