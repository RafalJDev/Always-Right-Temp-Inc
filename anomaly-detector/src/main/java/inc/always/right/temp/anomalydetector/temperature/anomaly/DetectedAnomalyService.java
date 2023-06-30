package inc.always.right.temp.anomalydetector.temperature.anomaly;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetectedAnomalyService {

    private final DetectedAnomalyRepository detectedAnomalyRepository;
    private final DetectedAnomalyMapper detectedAnomalyMapper;

    public void storeAnomaly(TemperatureMeasurement temperatureMeasurement) {
        DetectedAnomaly detectedAnomaly = detectedAnomalyMapper.mapTo(temperatureMeasurement);
        detectedAnomalyRepository.save(detectedAnomaly);
    }
}
