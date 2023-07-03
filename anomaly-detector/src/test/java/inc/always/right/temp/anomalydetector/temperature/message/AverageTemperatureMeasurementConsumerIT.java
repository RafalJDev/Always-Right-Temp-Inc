package inc.always.right.temp.anomalydetector.temperature.message;

import com.jupitertools.springtestredis.RedisTestContainer;
import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomaly;
import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomalyRepository;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Durations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureUnit.CELSIUS;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
        topics = {"temperature-measurements"}
)
@Testcontainers
@RedisTestContainer(
        hostTargetProperty = "spring.data.redis.host",
        portTargetProperty = "spring.data.redis.port"
)
@ActiveProfiles("test")
class AverageTemperatureMeasurementConsumerIT {

    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgresql = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testcontainer")
            .withUsername("user")
            .withPassword("pass");

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;

    @Autowired
    DetectedAnomalyRepository detectedAnomalyRepository;

    @Autowired
    RecentTemperatureMeasurementRepository recentTemperatureMeasurementRepository;

    @Test
    void givenAnomaly_shouldDetectAndSave() {

        List<String> temperatures = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        var thermometerId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        temperatures.forEach(
                temperature -> {
                    TemperatureMeasurement temp = new TemperatureMeasurement(thermometerId, roomId, new BigDecimal(temperature), now(), CELSIUS);
                    kafkaTemplate.send("temperature-measurements", temp);
                }
        );

        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            long count = recentTemperatureMeasurementRepository.count();
            assertEquals(temperatures.size(), count);
        });

        //when
        TemperatureMeasurement temp = new TemperatureMeasurement(thermometerId, roomId, new BigDecimal("27.1"), now(), CELSIUS);
        kafkaTemplate.send("temperature-measurements", temp);

        //then
        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            List<DetectedAnomaly> anomalies = detectedAnomalyRepository.findAll();

            assertEquals(1, anomalies.size());
            assertEquals(new BigDecimal("27.10"), anomalies.get(0).getTemperature());
        });
    }

    @Test
    void givenNotAnomaly_shouldNotDetect() {
        List<String> temperatures = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        var thermometerId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        temperatures.forEach(
                temperature -> {
                    TemperatureMeasurement temp = new TemperatureMeasurement(thermometerId, roomId, new BigDecimal(temperature), now(), CELSIUS);
                    kafkaTemplate.send("temperature-measurements", temp);
                }
        );

        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            long count = recentTemperatureMeasurementRepository.count();
            assertEquals(temperatures.size(), count);
        });

        //when
        TemperatureMeasurement temp = new TemperatureMeasurement(thermometerId, roomId, new BigDecimal("24.76"), now(), CELSIUS);
        kafkaTemplate.send("temperature-measurements", temp);

        //then
        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            List<DetectedAnomaly> anomalies = detectedAnomalyRepository.findAll();

            assertEquals(0, anomalies.size());
        });
    }

    @AfterEach
    void clean() {
        recentTemperatureMeasurementRepository.deleteAll();
        detectedAnomalyRepository.deleteAll();
    }

    @AfterAll
    static void teardown() {
        postgresql.stop();
    }
}