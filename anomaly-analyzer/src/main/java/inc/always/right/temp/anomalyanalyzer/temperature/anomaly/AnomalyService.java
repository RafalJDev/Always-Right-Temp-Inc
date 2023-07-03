package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class AnomalyService {

    @Value("${anomaly.detector.threshold: 2}")
    private Long threshold;

    private final AnomalyRepository repository;
    private final AnomalyMapper mapper;

    public Flux<DetectedAnomalyResponse> getAnomaliesByThermometerId(UUID thermometerId) {
        return repository.findAllByThermometerId(thermometerId.toString())
                .map(mapper::map);
    }


    public Flux<DetectedAnomalyResponse> getAnomaliesByRoomId(UUID roomId) {
        return repository.findAllByRoomId(roomId.toString())
                .map(mapper::map);
    }

    public Flux<AnomalyThresholdResult> getAnomaliesWithAmountHigherThanThreshold() {
        //todo fix it
//        return repository.findAllAnomaliesWithAmountHigherThanThreshold(threshold);
        //workaround for postgres issue
        return repository.findAll()
                .groupBy(DetectedAnomaly::getThermometerId)
                .flatMap(Flux::collectList)
                .map(detectedAnomalies -> new AnomalyThresholdResult(detectedAnomalies.get(0).getThermometerId(), detectedAnomalies.size()))
                .filter(result -> result.getAmountOfAnomalies() > threshold);
    }

    public Flux<String> streamAnomalies(String thermometerId) {
        return null;
    }

}
