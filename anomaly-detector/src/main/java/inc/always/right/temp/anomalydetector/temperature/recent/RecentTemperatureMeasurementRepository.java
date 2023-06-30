package inc.always.right.temp.anomalydetector.temperature.recent;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentTemperatureMeasurementRepository extends CrudRepository<RecentTemperatureMeasurement, String> {
    List<RecentTemperatureMeasurement> findAllByThermometerIdAndRoomId(String thermometerId, String roomId);

}
