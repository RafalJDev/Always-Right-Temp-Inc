package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AnomalyRepository extends ReactiveCrudRepository<DetectedAnomaly, Integer> {

    Flux<DetectedAnomaly> findAllByThermometerId(String thermometerId);

    Flux<DetectedAnomaly> findAllByRoomId(String roomId);

    //todo not working correctly (returns 0 amountOfAnomalies)
    @Query(value = "SELECT da.thermometer_id, count(da.id) as amountOfAnomalies"
            + " FROM Detected_Anomaly da GROUP BY da.thermometer_id HAVING count(da.id) > :threshold")
    Flux<AnomalyThresholdResult> findAllAnomaliesWithAmountHigherThanThreshold(Long threshold);


}
