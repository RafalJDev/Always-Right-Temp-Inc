package inc.always.right.temp.anomalydetector.temperature.detector;


import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomalyService;
import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
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
        if (strategy.findAnomalies(measurement)
                .foundAnomaly()) {
            detectedAnomalyService.storeAnomaly(measurement);
        }

        recentTemperatureMeasurementService.addRecentMeasurement(measurement);
    }
}
