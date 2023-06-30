package inc.always.right.temp.anomalydetector.temperature.service;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;

interface AnomalyDetectorStrategy {
    DetectorResult findAnomalies(TemperatureMeasurement measurement);
}
