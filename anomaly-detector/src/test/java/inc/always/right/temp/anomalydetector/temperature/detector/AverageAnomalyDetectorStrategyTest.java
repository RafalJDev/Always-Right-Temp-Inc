package inc.always.right.temp.anomalydetector.temperature.detector;

import inc.always.right.temp.anomalydetector.temperature.domain.TemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurement;
import inc.always.right.temp.anomalydetector.temperature.recent.RecentTemperatureMeasurementService;
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
    RecentTemperatureMeasurementService service;

    @ParameterizedTest
    @MethodSource(value = {
            "provideTemperatureMeasurements_aboveZero",
            "provideTemperatureMeasurements_belowZero",

            "provideTemperatureMeasurements2_aboveZero",
            "provideTemperatureMeasurements2_belowZero"
    })
    void givenAnomaly(
            List<RecentTemperatureMeasurement> recentMeasurements,
            TemperatureMeasurement temperatureMeasurement,
            boolean foundAnomaly
    ) {
        //given
        UUID thermometerId = temperatureMeasurement.thermometerId();
        UUID roomId = temperatureMeasurement.roomId();
        long limit = 10;
        when(service.getRecentMeasurements(thermometerId, roomId, limit)).thenReturn(recentMeasurements);


        //when
        DetectorResult result = strategy.findAnomalies(temperatureMeasurement);

        //then
        assertEquals(foundAnomaly, result.foundAnomaly());
    }

    private static Stream<Arguments> provideTemperatureMeasurements_aboveZero() {
        // 1. Anomaly - average = 19.77 current =27.1
        List<String> temperatures_19_77 = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1"
        );

        var recent_19_77 = createRecentMeasurements(temperatures_19_77);

        var temperatureMeasurement1 = createMeasurement("27.1");

        // #########################################################################################################
        // ##  Diff 5.00
        // 2. Not Anomaly - average = 19.77, current = 24.76, diff = 4.99
        var temperatureMeasurement2 = createMeasurement("24.76");

        // 3. Anomaly - average = 19.77, current = 24.77, diff = 5.00
        var temperatureMeasurement3 = createMeasurement("24.77");

        // 4. Anomaly - average = 19.77, current = 24.78, diff = 5.01
        var temperatureMeasurement4 = createMeasurement("24.78");

        // #########################################################################################################
        // ##  Diff -5.00
        // 5. Not Anomaly - average = 19.77, current = 24.78, diff = -4.99
        var temperatureMeasurement5 = createMeasurement("14.78");

        // 6. Anomaly - average = 19.77, current = 24.77, diff = -5.00
        var temperatureMeasurement6 = createMeasurement("14.77");

        // 7. Anomaly - average = 19.77, current = 24.76, diff = -5.01
        var temperatureMeasurement7 = createMeasurement("14.76");


        return Stream.of(
                Arguments.of(recent_19_77, temperatureMeasurement1, true),

                Arguments.of(recent_19_77, temperatureMeasurement2, false),
                Arguments.of(recent_19_77, temperatureMeasurement3, true),
                Arguments.of(recent_19_77, temperatureMeasurement4, true),

                Arguments.of(recent_19_77, temperatureMeasurement5, false),
                Arguments.of(recent_19_77, temperatureMeasurement6, true),
                Arguments.of(recent_19_77, temperatureMeasurement7, true)
        );
    }

    private static Stream<Arguments> provideTemperatureMeasurements_belowZero() {
        // #########################################################################################################
        // ## Diff 5.00
        List<String> temperatures_minus_19_77 = List.of(
                "-20.1", "-21.2", "-20.3", "-19.1", "-20.1", "-19.2", "-20.1", "-18.1", "-19.4", "-20.1"
        );
        var recent_minus_19_77 = createRecentMeasurements(temperatures_minus_19_77);

        // 8. Not Anomaly - average = -19.77, current = -14.78, diff = 4.99
        var temperatureMeasurement8 = createMeasurement("-14.78");

        // 9. Anomaly - average = -19.77, current = -14.76, diff = 5.00
        var temperatureMeasurement9 = createMeasurement("-14.77");

        // 10. Anomaly - average = -19.77, current = -14.76, diff = 5.01
        var temperatureMeasurement10 = createMeasurement("-14.76");

        // #########################################################################################################
        // ## Diff -5.00
        // 11. Not Anomaly - average = -19.77, current = -24.76, diff = -4.99
        var temperatureMeasurement11 = createMeasurement("-24.76");

        // 12. Anomaly - average = -19.77, current = -24.77, diff = -5.00
        var temperatureMeasurement12 = createMeasurement("-24.77");

        // 13. Anomaly - average = -19.77, current = -24.78, diff = -5.01
        var temperatureMeasurement13 = createMeasurement("-24.78");

        return Stream.of(
                // ## Diff - 5.00
                Arguments.of(recent_minus_19_77, temperatureMeasurement8, false),
                Arguments.of(recent_minus_19_77, temperatureMeasurement9, true),
                Arguments.of(recent_minus_19_77, temperatureMeasurement10, true),

                // ## Diff 5.00
                Arguments.of(recent_minus_19_77, temperatureMeasurement11, false),
                Arguments.of(recent_minus_19_77, temperatureMeasurement12, true),
                Arguments.of(recent_minus_19_77, temperatureMeasurement13, true)
        );
    }

    //second
    private static Stream<Arguments> provideTemperatureMeasurements2_aboveZero() {
        List<String> temperatures_20_27 = List.of(
                "20.1", "21.2", "20.3", "19.1", "20.1", "19.2", "20.1", "18.1", "19.4", "20.1",
                "21.1", "22.2", "21.3", "20.1", "21.1", "20.2", "21.1", "19.1", "20.4", "21.1"
        );

        var recent_20_27 = createRecentMeasurements(temperatures_20_27);

        // #########################################################################################################
        // ##  Diff 5.00
        // 14. Not Anomaly - average = 20.27, current = 25.26, diff = 4.99
        var temperatureMeasurement14 = createMeasurement("25.26");

        // 15. Anomaly - average = 20.27, current = 25.27, diff = 5.00
        var temperatureMeasurement15 = createMeasurement("25.27");

        // 16. Anomaly - average = 20.27, current = 25.28, diff = 5.01
        var temperatureMeasurement16 = createMeasurement("25.28");

        // #########################################################################################################
        // ##  Diff -5.00
        // 17. Not Anomaly - average = 20.27, current = 15.28, diff = -4.99
        var temperatureMeasurement17 = createMeasurement("15.28");

        // 18. Anomaly - average = 20.27, current = 15.27, diff = -5.00
        var temperatureMeasurement18 = createMeasurement("15.27");

        // 19. Anomaly - average = 20.27, current = 15.26, diff = -5.01
        var temperatureMeasurement19 = createMeasurement("15.26");


        return Stream.of(
                Arguments.of(recent_20_27, temperatureMeasurement14, false),
                Arguments.of(recent_20_27, temperatureMeasurement15, true),
                Arguments.of(recent_20_27, temperatureMeasurement16, true),

                Arguments.of(recent_20_27, temperatureMeasurement17, false),
                Arguments.of(recent_20_27, temperatureMeasurement18, true),
                Arguments.of(recent_20_27, temperatureMeasurement19, true)
        );
    }

    private static Stream<Arguments> provideTemperatureMeasurements2_belowZero() {
        // #########################################################################################################
        // ## Diff 5.00
        List<String> temperatures_minus_20_27 = List.of(
                "-20.1", "-21.2", "-20.3", "-19.1", "-20.1", "-19.2", "-20.1", "-18.1", "-19.4", "-20.1",
                "-21.1", "-22.2", "-21.3", "-20.1", "-21.1", "-20.2", "-21.1", "-19.1", "-20.4", "-21.1"
        );
        var recent_minus_20_27 = createRecentMeasurements(temperatures_minus_20_27);

        BigDecimal bigDecimal = AnomalyCalculator.calculateAverage(recent_minus_20_27);
        System.out.println("bigDecimal = " + bigDecimal);

        // 20. Not Anomaly - average = -20.27, current = -15.28, diff = 4.99
        var temperatureMeasurement20 = createMeasurement("-15.28");

        // 21. Anomaly - average = -20.27, current = -15.27, diff = 5.00
        var temperatureMeasurement21 = createMeasurement("-15.27");

        // 22. Anomaly - average = -20.27, current = -15.26, diff = 5.01
        var temperatureMeasurement22 = createMeasurement("-15.26");

        // #########################################################################################################
        // ## Diff -5.00
        // 23. Not Anomaly - average = -20.27, current = -25.26, diff = -4.99
        var temperatureMeasurement23 = createMeasurement("-25.26");

        // 24. Anomaly - average = -20.27, current = -25.27, diff = -5.00
        var temperatureMeasurement24 = createMeasurement("-25.27");

        // 25. Anomaly - average = -20.27, current = -25.28, diff = -5.01
        var temperatureMeasurement25 = createMeasurement("-25.28");

        return Stream.of(
                // ## Diff - 5.00
                Arguments.of(recent_minus_20_27, temperatureMeasurement20, false),
                Arguments.of(recent_minus_20_27, temperatureMeasurement21, true),
                Arguments.of(recent_minus_20_27, temperatureMeasurement22, true),

                // ## Diff 5.00
                Arguments.of(recent_minus_20_27, temperatureMeasurement23, false),
                Arguments.of(recent_minus_20_27, temperatureMeasurement24, true),
                Arguments.of(recent_minus_20_27, temperatureMeasurement25, true)
        );
    }

    private static List<RecentTemperatureMeasurement> createRecentMeasurements(List<String> recentMeasurements) {
        return recentMeasurements.stream()
                .map(temperature -> new RecentTemperatureMeasurement(null, null, null, new BigDecimal(temperature), now()))
                .toList();
    }

    private static TemperatureMeasurement createMeasurement(String val) {
        return new TemperatureMeasurement(null, null, new BigDecimal(val), now());
    }
}