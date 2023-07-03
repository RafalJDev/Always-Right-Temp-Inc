package inc.always.right.temp.anomalyanalyzer.temperature.anomaly;

import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)

public interface AnomalyMapper {

    DetectedAnomalyResponse map(DetectedAnomaly detectedAnomaly);
}
