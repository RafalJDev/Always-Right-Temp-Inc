package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import inc.always.right.temp.anomalyanalyzer.temperature.anomaly.notification.DetectedAnomalyNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/anomalies")
@RequiredArgsConstructor
class AnomalyController {

    private final AnomalyService service;
    private final DetectedAnomalyNotificationService notificationService;

    @GetMapping
    Flux<DetectedAnomalyResponse> anomaliesByThermometerIdOrRoomId(@RequestParam(required = false) UUID thermometerId,
                                                                   @RequestParam(required = false) UUID roomId) {

        System.out.println("thermometerId = " + thermometerId);
        if (thermometerId == null && roomId == null) {
            return Flux.just();
        }

        if (thermometerId != null) {
            return service.getAnomaliesByThermometerId(thermometerId);
        } else if (roomId != null) {
            return service.getAnomaliesByRoomId(roomId);
        }
        return Flux.just();
    }

    @GetMapping("/threshold")
    Flux<AnomalyThresholdResult> anomaliesAmountsHigherThanThreshold() {
        return service.getAnomaliesWithAmountHigherThanThreshold();
    }

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent> streamAnomalies(@RequestParam String thermometerId) {
        return notificationService.listenToSavedAnomalies();
    }

    @GetMapping(path = "/unsubscribe")
    Mono<ResponseEntity<Void>> streamAnomalies() {
        notificationService.unlistenToSavedItems();
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.I_AM_A_TEAPOT)
                        .body(null)
        );
    }

}
