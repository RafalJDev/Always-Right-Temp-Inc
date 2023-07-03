package inc.always.right.temp.anomalydetector.temperature.anomaly;

import inc.always.right.temp.anomalydetector.temperature.config.UUIDConverter;
import inc.always.right.temp.anomalydetector.temperature.measurement.Temperature;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureUnit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Detected_Anomaly")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class DetectedAnomaly implements Temperature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Convert(converter = UUIDConverter.class)
    private UUID thermometerId;
    @Column
    @Convert(converter = UUIDConverter.class)
    private UUID roomId;
    @Column
    private BigDecimal temperature;
    @Column
    private LocalDateTime timestamp;
    @Column
    @Enumerated(EnumType.STRING)
    private TemperatureUnit temperatureUnit;

    @Override
    public BigDecimal temperature() {
        return temperature;
    }

    @Override
    public TemperatureUnit temperatureUnit() {
        return temperatureUnit;
    }
}
