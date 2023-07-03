package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@ConditionalOnProperty(prefix = "anomaly.detector", name = "strategy", havingValue = "timestamp")
@RequiredArgsConstructor
@Slf4j
class TimestampAnomalyDetectorStrategy implements AnomalyDetectorStrategy {

    private final RecentTemperatureMeasurementService service;
    private final AnomalyCalculator calculator;

    @Value("${anomaly.detector.strategy.timestamp.from.seconds: 10}")
    private long timestampFromSeconds;

    @Override
    public DetectorResult findAnomalies(TemperatureMeasurement measurement) {
        List<RecentTemperatureMeasurement> recentMeasurements = service.getRecentMeasurements(measurement.thermometerId(), measurement.roomId(), now().minusSeconds(timestampFromSeconds));

        if (recentMeasurements.isEmpty()) {
            return DetectorResult.noAnomaly();
        }
        if (calculator.isAnomaly(recentMeasurements, measurement)) {
            return DetectorResult.anomalies(List.of(measurement));
        }

        return DetectorResult.noAnomaly();
    }
}
