package inc.always.right.temp.anomalyanalyzer.temperature.anomaly.notification;

import inc.always.right.temp.anomalyanalyzer.temperature.anomaly.DetectedAnomaly;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static inc.always.right.temp.anomalyanalyzer.temperature.anomaly.notification.NotificationTopic.ANOMALY_SAVED;

@Service
public class DetectedAnomalyNotificationService extends BaseNotificationService {

    public DetectedAnomalyNotificationService(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @PreDestroy
    private void preDestroy() {
        this.getConnection().close().subscribe();
    }

    public Flux<ServerSentEvent> listenToSavedAnomalies() {
        return Flux.just(listen(ANOMALY_SAVED, DetectedAnomaly.class))
                .map(o -> ServerSentEvent.builder()
                        .retry(Duration.ofSeconds(4L))
                        .event(o.getClass().getName())
                        .data(o).build()
                );
    }

    public void unlistenToSavedItems() {
        unlisten(ANOMALY_SAVED);
    }


}
