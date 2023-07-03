package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;

interface AnomalyDetectorStrategy {
    DetectorResult findAnomalies(TemperatureMeasurement measurement);
}
