package inc.always.right.temp.anomalydetector.temperature.service;

import inc.always.right.temp.anomalydetector.temperature.anomaly.DetectedAnomalyService;
import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.redis.RecentTemperatureMeasurementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectorFacadeTest {

    @InjectMocks
    AnomalyDetectorFacade facade;

    @Mock
    AnomalyDetectorStrategy strategy;
    @Mock
    DetectedAnomalyService detectedAnomalyService;
    @Mock
    RecentTemperatureMeasurementService recentTemperatureMeasurementService;

    @Test
    void givenNotAnomaly_thenShouldNotSave() {
        //given
        when(strategy.findAnomalies(any())).thenReturn(DetectorResult.noAnomaly());

        //when
        var notAnomaly = new TemperatureMeasurement(null, null, null, null);
        facade.handleMeasurement(notAnomaly);

        //then
        verify(detectedAnomalyService, never()).storeAnomaly(notAnomaly);
    }

    @Test
    void givenAnomaly_thenShouldSave() {
        //given
        TemperatureMeasurement anomaly = new TemperatureMeasurement(null, null, null, null);
        when(strategy.findAnomalies(any())).thenReturn(new DetectorResult(List.of(anomaly)));

        //when
        facade.handleMeasurement(anomaly);

        //then
        verify(detectedAnomalyService, atMostOnce()).storeAnomaly(anomaly);
    }


}