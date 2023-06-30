package inc.always.right.temp.anomalydetector.temperature.service;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CalculatorUtil {

    private static final BigDecimal ANOMALY_THRESHOLD = new BigDecimal("5.00");

    public static BigDecimal calculateAverage(List<RecentTemperatureMeasurement> recentMeasurements) {
        return recentMeasurements.stream()
                .map(RecentTemperatureMeasurement::temperature)
                .reduce(BigDecimal::add)
                .get()
                .setScale(2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(recentMeasurements.size()));
    }

    public static boolean isAnomaly(TemperatureMeasurement measurement, BigDecimal average) {
        return average.abs()
                .subtract(measurement.temperature().abs())
                .abs()
                .compareTo(ANOMALY_THRESHOLD)
                >= 0;
    }

}
