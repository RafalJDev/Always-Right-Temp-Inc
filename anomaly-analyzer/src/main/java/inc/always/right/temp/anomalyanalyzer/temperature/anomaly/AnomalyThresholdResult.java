package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyThresholdResult {

    private UUID thermometerId;
    private long amountOfAnomalies;

    public AnomalyThresholdResult(String thermometerId, long amountOfAnomalies) {
        this.thermometerId = UUID.fromString(thermometerId);
        this.amountOfAnomalies = amountOfAnomalies;
    }
}
