package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;

import java.util.List;

public record DetectorResult(
        List<TemperatureMeasurement> anomalies
) {

    public boolean foundAnomaly() {
        return anomalies != null && !anomalies.isEmpty();
    }

    public static DetectorResult noAnomaly() {
        return new DetectorResult(List.of());

    }
    public static DetectorResult anomalies(List<TemperatureMeasurement> anomalies) {
        return new DetectorResult(anomalies);
    }

}
