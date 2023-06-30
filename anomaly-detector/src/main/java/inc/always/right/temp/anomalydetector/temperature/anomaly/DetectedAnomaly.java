package inc.always.right.temp.anomalydetector.temperature.anomaly;

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
public class DetectedAnomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private UUID thermometerId;
    @Column
    private UUID roomId;
    @Column
    private BigDecimal temperature;
    @Column
    private LocalDateTime timestamp;


}
