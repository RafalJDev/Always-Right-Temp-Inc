package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static inc.always.right.temp.anomalydetector.temperature.detector.AnomalyCalculator.calculateAverage;
import static inc.always.right.temp.anomalydetector.temperature.detector.AnomalyCalculator.isAnomaly;

@Service
@Primary
@ConditionalOnProperty(prefix = "anomaly.detector", name = "strategy", havingValue = "average")
@RequiredArgsConstructor
@Slf4j
class AverageAnomalyDetectorStrategy implements AnomalyDetectorStrategy {

    private static final long AMOUNT_OF_MEASUREMENTS = 10;

    private final RecentTemperatureMeasurementService service;

    @Override
    public DetectorResult findAnomalies(TemperatureMeasurement measurement) {
        // todo change to redistemplate to make query on redis itself and not to get all related rows
        // but assuming 1 measurement per second means max 60 record would be pulled here (ttl 6o sec)
        List<RecentTemperatureMeasurement> recentMeasurements = service.getRecentMeasurements(measurement.thermometerId(), measurement.roomId(), AMOUNT_OF_MEASUREMENTS);

        if (recentMeasurements.size() < AMOUNT_OF_MEASUREMENTS) {
            log.debug("Not enough recent measurements to detect anomalies, thermometerId: {}, roomId: {}", measurement.thermometerId(), measurement.roomId());
            return DetectorResult.noAnomaly();
        }

        BigDecimal average = calculateAverage(recentMeasurements);

        if (isAnomaly(measurement, average)) {
            log.info("Found anomaly: {}", measurement);
            return DetectorResult.anomalies(List.of(measurement));
        }

        return DetectorResult.noAnomaly();
    }

}
