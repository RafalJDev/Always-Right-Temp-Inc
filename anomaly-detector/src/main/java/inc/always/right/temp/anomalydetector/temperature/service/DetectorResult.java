package inc.always.right.temp.anomalydetector.temperature.service;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;

import java.util.List;

public record DetectorResult(
        List<TemperatureMeasurement> anomalies
) {

    public boolean foundAnomaly() {
        return anomalies != null && !anomalies.isEmpty();
    }

    public static DetectorResult noAnomaly() {
        return new DetectorResult(null);

    }
    public static DetectorResult anomalies(List<TemperatureMeasurement> anomalies) {
        return new DetectorResult(anomalies);
    }

}
