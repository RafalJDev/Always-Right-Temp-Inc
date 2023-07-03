package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementRepository;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${anomaly.detector.threshold: 10}")
    private Long amountOfMeasurements;

    private final RecentTemperatureMeasurementService service;
    private final RecentTemperatureMeasurementRepository repository;

    @Override
    public DetectorResult findAnomalies(TemperatureMeasurement measurement) {
        List<RecentTemperatureMeasurement> recentMeasurements = service.getRecentMeasurements(measurement.thermometerId(), measurement.roomId(), amountOfMeasurements);

        if (measurement.temperature().compareTo(new BigDecimal(25.00)) > 0) {
            System.out.println("here2");
        }

        if (recentMeasurements.size() < amountOfMeasurements) {
            log.info("Not enough recent measurements to detect anomalies, thermometerId: {}, roomId: {}", measurement.thermometerId(), measurement.roomId());
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
