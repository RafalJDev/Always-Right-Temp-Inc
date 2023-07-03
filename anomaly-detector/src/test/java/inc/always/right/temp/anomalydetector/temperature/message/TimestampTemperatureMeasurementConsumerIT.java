package inc.always.right.temp.anomalydetector.temperature.message;

import com.jupitertools.springtestredis.RedisTestContainer;
import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomaly;
import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomalyRepository;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Durations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(properties = { "anomaly.detector.strategy=timestamp" })
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" },
        topics = {"temperature-measurements"}
)
@Testcontainers
@RedisTestContainer(
        hostTargetProperty = "spring.data.redis.host",
        portTargetProperty = "spring.data.redis.port"
)
class TimestampTemperatureMeasurementConsumerIT {

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

    @Test
    void givenAnomaly_shouldDetectAndSave() {

        List<Double> temperatures = List.of(
                20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 27.1
        );

        var thermometerId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        temperatures.forEach(
                temperature -> {
                    TemperatureMeasurement temp = new TemperatureMeasurement(thermometerId, roomId, new BigDecimal(temperature), LocalDateTime.now());

                    kafkaTemplate.send("temperature-measurements", temp);
                }
        );

        //then
        await().atMost(Durations.ONE_MINUTE).untilAsserted(() -> {

            List<DetectedAnomaly> anomalies = detectedAnomalyRepository.findAll();

            assertEquals(1, anomalies.size());
            assertEquals(new BigDecimal("27.10"), anomalies.get(0).getTemperature());
        });
    }

    @Test
    void givenNotAnomaly_shouldNotDetect() {

        List<Double> temperatures = List.of(
                20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 24.76
        );

        var thermometerId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        temperatures.forEach(
                temperature -> {
                    TemperatureMeasurement temp = new TemperatureMeasurement(thermometerId, roomId, new BigDecimal(temperature), LocalDateTime.now());

                    kafkaTemplate.send("temperature-measurements", temp);
                }
        );

        //then
        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            List<DetectedAnomaly> anomalies = detectedAnomalyRepository.findAll();

            assertEquals(0, anomalies.size());
        });
    }

    @AfterAll
    static void teardown() {
        postgresql.stop();
    }
}