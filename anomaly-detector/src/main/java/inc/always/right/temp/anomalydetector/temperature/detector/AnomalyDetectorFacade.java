package inc.always.right.temp.anomalydetector.temperature.detector;


import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomalyService;
import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnomalyDetectorFacade {

    private final AnomalyDetectorStrategy strategy;
    private final DetectedAnomalyService detectedAnomalyService;
    private final RecentTemperatureMeasurementService recentTemperatureMeasurementService;

    public void handleMeasurement(TemperatureMeasurement measurement) {
        strategy.findAnomalies(measurement)
                .anomalies()
                .forEach(detectedAnomalyService::storeAnomaly);

        recentTemperatureMeasurementService.addRecentMeasurement(measurement);
    }
}
