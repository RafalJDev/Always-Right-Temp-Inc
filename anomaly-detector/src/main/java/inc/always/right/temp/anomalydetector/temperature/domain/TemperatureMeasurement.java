package inc.always.right.temp.anomalydetector.temperature.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//@ToString
public record TemperatureMeasurement(
        UUID thermometerId,
        UUID roomId,
        BigDecimal temperature,
        LocalDateTime timestamp
) {
}
