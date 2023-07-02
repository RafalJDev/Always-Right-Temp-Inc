package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static inc.always.right.temp.anomalydetector.temperature.detector.AnomalyCalculator.calculateAverage;
import static inc.always.right.temp.anomalydetector.temperature.detector.AnomalyCalculator.isAnomaly;
import static java.time.LocalDateTime.now;

@Service
@ConditionalOnProperty(prefix = "anomaly.detector", name = "strategy", havingValue = "timestamp")
@RequiredArgsConstructor
@Slf4j
class TimestampAnomalyDetectorStrategy implements AnomalyDetectorStrategy {

    private final RecentTemperatureMeasurementService service;

    @Override
    public DetectorResult findAnomalies(TemperatureMeasurement measurement) {
        List<RecentTemperatureMeasurement> recentMeasurements = service.getRecentMeasurements(measurement.thermometerId(), measurement.roomId(), now().minusSeconds(10));

        BigDecimal average = calculateAverage(recentMeasurements);

        if (isAnomaly(measurement, average)) {
            return DetectorResult.anomalies(List.of(measurement));
        }

        return DetectorResult.noAnomaly();
    }
}
