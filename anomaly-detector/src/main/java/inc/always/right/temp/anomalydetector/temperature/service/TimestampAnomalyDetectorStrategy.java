package inc.always.right.temp.anomalydetector.temperature.service;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static inc.always.right.temp.anomalydetector.temperature.service.CalculatorUtil.calculateAverage;
import static inc.always.right.temp.anomalydetector.temperature.service.CalculatorUtil.isAnomaly;
import static java.time.LocalDateTime.now;

@Service
@ConditionalOnProperty(prefix = "anomaly.detector", name = "strategy", havingValue = "timestamp")
@RequiredArgsConstructor
@Slf4j
class TimestampAnomalyDetectorStrategy implements AnomalyDetectorStrategy {

    @Override
    public DetectorResult findAnomalies(TemperatureMeasurement measurement) {

        return DetectorResult.noAnomaly();
    }
}
