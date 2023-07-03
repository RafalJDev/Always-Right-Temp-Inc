package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureConverter;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
class AnomalyCalculator {


    private final BigDecimal ANOMALY_THRESHOLD;


    private final int scale;

    private final TemperatureConverter temperatureConverter;

    public AnomalyCalculator(@Value("${anomaly.detector.calculator.threshold: 5.00}") String anomalyThreshold,
                             @Value("${anomaly.detector.calculator.scale: 2}")int scale,
                             TemperatureConverter temperatureConverter
    ) {
        this.ANOMALY_THRESHOLD = new BigDecimal(anomalyThreshold);
        this.scale = scale;
        this.temperatureConverter = temperatureConverter;
    }


    boolean isAnomaly(List<RecentTemperatureMeasurement> recentMeasurements, TemperatureMeasurement measurement) {
        BigDecimal average = calculateAverage(recentMeasurements);

        return average.abs()
                .subtract(temperatureConverter.extractTemperatureAsCelsius(measurement).abs())
                .abs()
                .compareTo(ANOMALY_THRESHOLD)
                >= 0;
    }

    BigDecimal calculateAverage(List<RecentTemperatureMeasurement> recentMeasurements) {

        return recentMeasurements.stream()
                .map(temperatureConverter::extractTemperatureAsCelsius)
                .reduce(BigDecimal::add)
                .get()
                .divide(BigDecimal.valueOf(recentMeasurements.size()), scale, RoundingMode.HALF_UP);
    }

}
