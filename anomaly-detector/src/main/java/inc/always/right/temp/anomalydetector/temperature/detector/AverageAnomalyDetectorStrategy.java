package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Primary
@ConditionalOnProperty(prefix = "anomaly.detector", name = "strategy", havingValue = "average")
@RequiredArgsConstructor
@Slf4j
class AverageAnomalyDetectorStrategy implements AnomalyDetectorStrategy {

    @Value("${anomaly.detector.average.min.amount: 10}")
    private Long minAmountOfMeasurements;

    private final RecentTemperatureMeasurementService service;
    private final AnomalyCalculator anomalyCalculator;

    @Override
    public DetectorResult findAnomalies(TemperatureMeasurement measurement) {
        List<RecentTemperatureMeasurement> recentMeasurements = service.getRecentMeasurements(measurement.thermometerId(), measurement.roomId(), minAmountOfMeasurements);

        if (recentMeasurements.size() < minAmountOfMeasurements) {
            log.info("Not enough recent measurements to detect anomalies, thermometerId: {}, roomId: {}", measurement.thermometerId(), measurement.roomId());
            return DetectorResult.noAnomaly();
        }

        if (anomalyCalculator.isAnomaly(recentMeasurements, measurement)) {
            log.info("Found anomaly: {}", measurement);
            return DetectorResult.anomalies(List.of(measurement));
        }

        return DetectorResult.noAnomaly();
    }

}
