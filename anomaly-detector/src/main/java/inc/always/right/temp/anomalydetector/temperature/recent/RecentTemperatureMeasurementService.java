package inc.always.right.temp.anomalydetector.temperature.recent;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecentTemperatureMeasurementService {

    private final RecentTemperatureMeasurementRepository repository;
    private final RecentTemperatureMeasurementMapper mapper;

    public void addRecentMeasurement(TemperatureMeasurement measurement) {
        RecentTemperatureMeasurement recent = mapper.map(measurement);
        repository.save(recent);
    }

    public List<RecentTemperatureMeasurement> getRecentMeasurements(UUID thermometerId, UUID roomId, Long limit) {
        // todo change to redistemplate to make query on redis itself and not to get all related rows
        // but assuming 1 measurement per second means max 60 record would be pulled here (ttl 6o sec)
        return repository.findAllByThermometerIdAndRoomId(thermometerId.toString(), roomId.toString())
                .stream()
                .sorted((m1, m2) -> m2.timestamp().compareTo(m1.timestamp()))
                .limit(limit)
                .toList();
    }

    public List<RecentTemperatureMeasurement> getRecentMeasurements(UUID thermometerId, UUID roomId, LocalDateTime from) {
        // todo change to redistemplate to make query on redis itself and not to get all related rows
        // but assuming 1 measurement per second means max 60 record would be pulled here (ttl 6o sec)
        return repository.findAllByThermometerIdAndRoomId(thermometerId.toString(), roomId.toString())
                .stream()
                .filter(recent -> from.isAfter(recent.timestamp()))
                .toList();
    }
}
