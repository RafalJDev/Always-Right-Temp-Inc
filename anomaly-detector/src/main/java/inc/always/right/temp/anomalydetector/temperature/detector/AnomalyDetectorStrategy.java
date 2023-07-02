package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;

interface AnomalyDetectorStrategy {
    DetectorResult findAnomalies(TemperatureMeasurement measurement);
}
