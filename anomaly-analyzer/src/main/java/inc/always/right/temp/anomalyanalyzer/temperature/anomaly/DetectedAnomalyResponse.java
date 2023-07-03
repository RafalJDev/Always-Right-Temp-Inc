package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class DetectedAnomalyResponse {

     UUID thermometerId;
     UUID roomId;
     BigDecimal temperature;
     LocalDateTime timestamp;

}
