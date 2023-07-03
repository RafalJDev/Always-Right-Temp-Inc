package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static inc.always.right.temp.anomalyanalyzer.temperature.anomaly.TemperatureUnit.CELSIUS;

@SpringBootTest
@Testcontainers
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AnomalyServiceIT {

    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:14.1-alpine")
            .withDatabaseName("testcontainer")
            .withUsername("user")
            .withPassword("pass");
    @Autowired
    AnomalyRepository repository;
    @Autowired
    AnomalyService service;

    @Test
    void shouldFindByThermometerId() {
        UUID thermometerId = UUID.randomUUID();
        var anomaly = new DetectedAnomaly(null, thermometerId.toString(), UUID.randomUUID().toString(), new BigDecimal("27.1"), LocalDateTime.now(), CELSIUS);

        StepVerifier.create(repository.save(anomaly))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(service.getAnomaliesByThermometerId(thermometerId))
                .expectNextMatches(measurement -> measurement.getThermometerId().equals(thermometerId))
                .verifyComplete();
    }

    @Test
    void shouldFindByRoomId() {
        UUID roomId = UUID.randomUUID();
        var anomaly = new DetectedAnomaly(null, UUID.randomUUID().toString(), roomId.toString(), new BigDecimal("27.1"), LocalDateTime.now(), CELSIUS);

        StepVerifier.create(repository.save(anomaly))
                .expectNextCount(1)
                .verifyComplete();

                StepVerifier.create(service.getAnomaliesByRoomId(roomId))
                .expectNextMatches(measurement -> measurement.getRoomId().equals(roomId))
                .verifyComplete();

    }

    @Test
    void shouldFindAnomaliesAboveThreshold() {
        UUID thermometerId = UUID.randomUUID();
        UUID thermometerId2 = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        var anomaly = new DetectedAnomaly(null, thermometerId.toString(), roomId.toString(), new BigDecimal("21.1"), LocalDateTime.now(), CELSIUS);
        var anomaly2 = new DetectedAnomaly(null, thermometerId.toString(), roomId.toString(), new BigDecimal("22.1"), LocalDateTime.now(), CELSIUS);
        var anomaly3 = new DetectedAnomaly(null, thermometerId.toString(), roomId.toString(), new BigDecimal("23.1"), LocalDateTime.now(), CELSIUS);

        var anomaly4 = new DetectedAnomaly(null, thermometerId2.toString(), roomId.toString(), new BigDecimal("24.1"), LocalDateTime.now(), CELSIUS);
        var anomaly5 = new DetectedAnomaly(null, thermometerId2.toString(), roomId.toString(), new BigDecimal("25.1"), LocalDateTime.now(), CELSIUS);

        StepVerifier.create(repository.saveAll(List.of(anomaly, anomaly2, anomaly3, anomaly4, anomaly5)))
                .expectNextCount(5)
                .verifyComplete();

        StepVerifier.create(service.getAnomaliesWithAmountHigherThanThreshold())
                .expectNextMatches(result -> result.getThermometerId().equals(thermometerId) && result.getAmountOfAnomalies() == 3)
                .verifyComplete();
    }

    @Test
    void testEventStream() {
        //todo
    }

}