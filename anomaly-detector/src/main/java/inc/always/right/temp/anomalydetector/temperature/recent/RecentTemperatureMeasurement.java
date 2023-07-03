package inc.always.right.temp.anomalydetector.temperature.recent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import inc.always.right.temp.anomalydetector.temperature.measurement.Temperature;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureUnit;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RedisHash(value = "RecentTemperatureMeasurement", timeToLive = 60L)
public record RecentTemperatureMeasurement(String id,
                                           @Indexed String thermometerId,
                                           @Indexed String roomId,
                                           BigDecimal temperature,
                                           @JsonDeserialize(using = LocalDateTimeDeserializer.class) @JsonSerialize(using = LocalDateTimeSerializer.class) LocalDateTime timestamp,

                                           TemperatureUnit temperatureUnit
) implements Temperature {

}
