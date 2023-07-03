package inc.always.right.temp.anomalydetector.temperature.detector;

import com.jupitertools.springtestredis.RedisTestContainer;
import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomaly;
import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomalyRepository;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RedisTestContainer(
        hostTargetProperty = "spring.data.redis.host",
        portTargetProperty = "spring.data.redis.port"
)
@Testcontainers
class AnomalyDetectorFacadeIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgresql = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testcontainer")
            .withUsername("user")
            .withPassword("pass");

    @Autowired
    AnomalyDetectorFacade facade;
    @Autowired
    RecentTemperatureMeasurementRepository recentTemperatureMeasurementRepository;

    @Autowired
    DetectedAnomalyRepository detectedAnomalyRepository;

    @Test
    void givenAnomaly() {
        List<String> temperatures = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        String thermometerId = UUID.randomUUID().toString();
        String roomId = UUID.randomUUID().toString();

        temperatures.forEach(
                temperature -> {
                    recentTemperatureMeasurementRepository.save(
                            new RecentTemperatureMeasurement(
                                    null,
                                    thermometerId,
                                    roomId,
                                    new BigDecimal(temperature),
                                    LocalDateTime.now()
                            ));
                }
        );

        TemperatureMeasurement measurement = new TemperatureMeasurement(UUID.fromString(thermometerId), UUID.fromString(roomId), new BigDecimal("27.1"), null);

        //when
        facade.handleMeasurement(measurement);

        //then
        List<DetectedAnomaly> anomalies = detectedAnomalyRepository.findAll();

        assertEquals(1, anomalies.size());
        assertEquals(new BigDecimal("27.10"), anomalies.get(0).getTemperature());
    }

    @AfterEach
    void tearDown() {
        recentTemperatureMeasurementRepository.deleteAll();
        detectedAnomalyRepository.deleteAll();
    }

    @AfterAll
    static void teardown() {
        postgresql.stop();
    }

}