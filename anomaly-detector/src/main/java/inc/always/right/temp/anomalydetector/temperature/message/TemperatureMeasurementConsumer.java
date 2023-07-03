package inc.always.right.temp.anomalydetector.temperature.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.always.right.temp.anomalydetector.temperature.detector.AnomalyDetectorFacade;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TemperatureMeasurementConsumer {

    private final AnomalyDetectorFacade facade;
    private final ObjectMapper objectMapper;

    private final ExecutorService executorService;

    @KafkaListener(
            topics = "temperature-measurements",
            concurrency = "20")
    public void receive(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        var message = consumerRecord.value();
        TemperatureMeasurement temperatureMeasurement = objectMapper.readValue(message, TemperatureMeasurement.class);
        log.info("Received temperature measurement: " + temperatureMeasurement);

        executorService.submit(() -> facade.handleMeasurement(temperatureMeasurement));
    }
}
