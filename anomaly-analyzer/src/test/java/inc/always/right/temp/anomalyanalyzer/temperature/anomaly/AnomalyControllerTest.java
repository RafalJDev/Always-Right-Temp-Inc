package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AnomalyController.class)
class AnomalyControllerTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    AnomalyService service;

    @MockBean
    AnomalyRepository repository;

    @Test
    void sth() {
        var thermometerId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        var measurement = new DetectedAnomalyResponse(thermometerId, roomId, new BigDecimal("27.1"), now());

        when(service.getAnomaliesByThermometerId(any())).thenReturn(Flux.just(measurement));

        webClient.get()
                .uri("/anomalies?thermometerId=" + thermometerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].thermometerId").isEqualTo(measurement.getThermometerId().toString())
                .jsonPath("$[0].roomId").isEqualTo(measurement.getRoomId().toString())
                .jsonPath("$[0].temperature").isEqualTo(measurement.getTemperature())
                .jsonPath("$[0].timestamp").isNotEmpty()
        ;

        verify(service, times(1)).getAnomaliesByThermometerId(any());
    }

    @Test
    void sth2() {

        webClient.get()
                .uri("/anomalies?thermometerId=")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$")
                .value(Matchers.empty());

        verify(service, never()).getAnomaliesByThermometerId(any());
    }

    @Test
    void sth3() {
        webClient.get()
                .uri("/anomalies")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$")
                .value(Matchers.empty());

        verify(service, never()).getAnomaliesByThermometerId(any());
    }
}