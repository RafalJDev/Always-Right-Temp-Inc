package inc.always.right.temp.anomalydetector.temperature.anomaly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectedAnomalyRepository extends JpaRepository<DetectedAnomaly, Long> {

}
