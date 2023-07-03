package inc.always.right.temp.anomalydetector.temperature.recent;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RecentTemperatureMeasurementMapper {

    RecentTemperatureMeasurement map(TemperatureMeasurement measurement);
}
