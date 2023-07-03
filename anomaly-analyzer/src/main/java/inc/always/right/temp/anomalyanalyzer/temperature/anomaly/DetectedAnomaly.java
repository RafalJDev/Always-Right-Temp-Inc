package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("Detected_Anomaly")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class DetectedAnomaly {

    @Id
    private Long id;

    @Column
//    @Convert(converter = UUIDConverter.class)
    private String thermometerId;
    @Column
//    @Convert(converter = UUIDConverter.class)
    private String roomId;
    @Column
    private BigDecimal temperature;
    @Column
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    @Column
    private TemperatureUnit temperatureUnit;

}
