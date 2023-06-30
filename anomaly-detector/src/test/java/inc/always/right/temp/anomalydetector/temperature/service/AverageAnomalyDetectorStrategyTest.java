package inc.always.right.temp.anomalydetector.temperature.service;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.redis.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.redis.RecentTemperatureMeasurementRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class AverageAnomalyDetectorStrategyTest {

    @InjectMocks
    AverageAnomalyDetectorStrategy strategy;

    @Mock
    RecentTemperatureMeasurementRepository repository;

    @ParameterizedTest
    @MethodSource("provideTemperatureMeasurements")
    void givenAnomaly(
            List<RecentTemperatureMeasurement> recentMeasurements,
            TemperatureMeasurement temperatureMeasurement,
            boolean foundAnomaly
    ) {
        //given
        String thermometerId = temperatureMeasurement.thermometerId().toString();
        String roomId = temperatureMeasurement.roomId().toString();
        when(repository.findAllByThermometerIdAndRoomId(thermometerId, roomId)).thenReturn(recentMeasurements);


        //when
//        var measurement = new TemperatureMeasurement(27.1, now(), roomId, thermometerId);
        DetectorResult result = strategy.findAnomalies(temperatureMeasurement);

        //then
        assertEquals(foundAnomaly, result.foundAnomaly());
    }

    private static Stream<Arguments> provideTemperatureMeasurements() {
        // 1. Anomaly - average = 19.77 current =27.1
        List<String> temperatures_19_77 = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        var thermometerId1 = UUID.randomUUID();
        var roomId1 = UUID.randomUUID();
        var recent_19_77 = createRecentMeasurements(temperatures_19_77, thermometerId1, roomId1);

        var temperatureMeasurement1 = createMeasurement(thermometerId1, roomId1, "27.1");

        // #########################################################################################################
        // #### Above zero
        // ##  Diff 5.00
        // 2. Not Anomaly - average = 19.77, current = 24.76, diff = 4.99
        var temperatureMeasurement2 = createMeasurement(thermometerId1, roomId1, "24.76");

        // 3. Anomaly - average = 19.77, current = 24.77, diff = 5.00
        var temperatureMeasurement3 = createMeasurement(thermometerId1, roomId1, "24.77");

        // 4. Anomaly - average = 19.77, current = 24.78, diff = 5.01
        var temperatureMeasurement4 = createMeasurement(thermometerId1, roomId1, "24.78");

        // #########################################################################################################
        // #### Above zero
        // ##  Diff -5.00
        // 5. Not Anomaly - average = 19.77, current = 24.76, diff = 4.99
        var temperatureMeasurement5 = createMeasurement(thermometerId1, roomId1, "14.76");

        // 6. Anomaly - average = 19.77, current = 24.77, diff = 5.00
        var temperatureMeasurement6 = createMeasurement(thermometerId1, roomId1, "14.77");

        // 7. Anomaly - average = 19.77, current = 24.78, diff = 5.01
        var temperatureMeasurement7 = createMeasurement(thermometerId1, roomId1, "14.78");

        // #########################################################################################################
        // #### Below Zero
        // ## Diff 5.00
        List<String> temperatures_minus_19_77 = List.of(
                "-20.1", "-21.2", "-20.3", "-19.1", "-20.1", "-19.2", "-20.1", "-18.1", "-19.4", "-20.1"
        );
        var recent_minus_19_77 = createRecentMeasurements(temperatures_minus_19_77, thermometerId1, roomId1);

        // 8. Not Anomaly - average = -19.77, current = -14.76, diff = 4.99
        var temperatureMeasurement8 = createMeasurement(thermometerId1, roomId1, "-14.76");

        // 9. Anomaly - average = -19.77, current = -14.76, diff = 5.00
        var temperatureMeasurement9 = createMeasurement(thermometerId1, roomId1, "-14.77");

        // 10. Anomaly - average = -19.77, current = -14.78, diff = 5.01
        var temperatureMeasurement10 = createMeasurement(thermometerId1, roomId1, "-14.78");

        // #########################################################################################################
        // ## Diff -5.00
        // 11. Not Anomaly - average = -19.77, current = -24.76, diff = 4.99
        var temperatureMeasurement11 = createMeasurement(thermometerId1, roomId1, "-24.76");

        // 12. Anomaly - average = -19.77, current = -24.77, diff = 5.00
        var temperatureMeasurement12 = createMeasurement(thermometerId1, roomId1, "-24.77");

        // 13. Anomaly - average = -19.77, current = -24.78, diff = 5.01
        var temperatureMeasurement13 = createMeasurement(thermometerId1, roomId1, "-24.78");

        return Stream.of(
                Arguments.of(recent_19_77, temperatureMeasurement1, true),

                Arguments.of(recent_19_77, temperatureMeasurement2, false),
                Arguments.of(recent_19_77, temperatureMeasurement3, true),
                Arguments.of(recent_19_77, temperatureMeasurement4, true),

                Arguments.of(recent_19_77, temperatureMeasurement5, true),
                Arguments.of(recent_19_77, temperatureMeasurement6, true),
                Arguments.of(recent_19_77, temperatureMeasurement7, false),

                // #### Below Zero
                // ## Diff - 5.00

                Arguments.of(recent_minus_19_77, temperatureMeasurement8, true),
                Arguments.of(recent_minus_19_77, temperatureMeasurement9, true),
                Arguments.of(recent_minus_19_77, temperatureMeasurement10, false),

                // ## Diff 5.00

                Arguments.of(recent_minus_19_77, temperatureMeasurement11, false),
                Arguments.of(recent_minus_19_77, temperatureMeasurement12, true),
                Arguments.of(recent_minus_19_77, temperatureMeasurement13, true)
        );
    }

    private static List<RecentTemperatureMeasurement> createRecentMeasurements(List<String> recentMeasurements, UUID thermometerId, UUID roomId) {
        return recentMeasurements.stream()
                .map(temperature -> new RecentTemperatureMeasurement(null, thermometerId.toString(), roomId.toString(), new BigDecimal(temperature), now()))
                .toList();
    }

    private static TemperatureMeasurement createMeasurement(UUID thermometerId1, UUID roomId1, String val) {
        return new TemperatureMeasurement(thermometerId1, roomId1, new BigDecimal(val), now());
    }
}