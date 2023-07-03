package inc.always.right.temp.anomalyanalyzer.temperature.anomaly.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.always.right.temp.anomalyanalyzer.temperature.anomaly.DetectedAnomaly;
import io.micrometer.common.util.StringUtils;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Wrapped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

//credits to https://github.com/Koziolek/wjug-r2dbc
@RequiredArgsConstructor
@Slf4j
class BaseNotificationService {

    private PostgresqlConnection connection;
    protected final ConnectionFactory connectionFactory;
    private final Set<NotificationTopic> watchedTopics = Collections.synchronizedSet(new HashSet<>());

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected Flux<DetectedAnomaly> listen(final NotificationTopic topic, final Class<DetectedAnomaly> clazz) {

        if(!watchedTopics.contains(topic)) {
            executeListenStatement(topic);
        }

        return getConnection().getNotifications()
                .log("notifications")
                .filter(notification -> topic.name().equals(notification.getName()) && notification.getParameter() != null)
                .handle((notification, sink) -> {
                    final String json = notification.getParameter();
                    if(!StringUtils.isBlank(json)) {
                        try {
                            sink.next(objectMapper.readValue(json, clazz));
                        } catch(JsonProcessingException e) {
                            log.error(String.format("Problem deserializing an instance of [%s] " +
                                    "with the following json: %s ", clazz.getSimpleName(), json), e);
                            Mono.error(e);
                        }
                    }
                });
    }

    protected void unlisten(final NotificationTopic topic) {
        if(watchedTopics.contains(topic)) {
            executeUnlistenStatement(topic);
        }
    }

    protected PostgresqlConnection getConnection() {
        if(connection == null) {
            synchronized(DetectedAnomalyNotificationService.class) {
                if(connection == null) {
                    try {
                        connection = Mono.from(connectionFactory.create())
                                .cast(Wrapped.class)
                                .map(Wrapped::unwrap)
                                .cast(PostgresqlConnection.class)
                                .toFuture().get();
                    } catch(InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch(ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return this.connection;
    }

    private void executeListenStatement(final NotificationTopic topic) {
        getConnection().createStatement(String.format("LISTEN \"%s\"", topic)).execute()
                .doOnComplete(() -> watchedTopics.add(topic))
                .subscribe();
    }

    private void executeUnlistenStatement(final NotificationTopic topic) {
        getConnection().createStatement(String.format("UNLISTEN \"%s\"", topic)).execute()
                .doOnComplete(() -> watchedTopics.remove(topic))
                .subscribe();
    }
}
