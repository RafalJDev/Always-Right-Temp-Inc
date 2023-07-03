package inc.always.right.temp.anomalydetector.temperature.anomaly;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DetectedAnomalyMapper {

    @Mapping(target = "id", ignore = true)
    DetectedAnomaly mapTo(TemperatureMeasurement temperatureMeasurement);
}
