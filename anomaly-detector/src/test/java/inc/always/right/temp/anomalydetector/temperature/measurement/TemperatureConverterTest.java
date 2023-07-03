package inc.always.right.temp.anomalydetector.temperature.measurement;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureConverterTest {

    TemperatureConverter converter = new TemperatureConverter(2);

    @Test
    void convertsToCelsiusCorrectly() {
        BigDecimal celsius = converter.fahrenheitToCelsius(new BigDecimal("1"));

        assertEquals(new BigDecimal("-17.22"), celsius);
    }

    @Test
    void convertsToFahrenheitCorrectly() {
        BigDecimal celsius = converter.celsiusToFahrenheit(new BigDecimal("1"));

        assertEquals(new BigDecimal("33.80"), celsius);
    }

    @Test
    void shouldExtractCelsius() {
        Temperature temperature = new TemperatureMeasurement(null, null, new BigDecimal("33.80"), null, TemperatureUnit.CELSIUS);
        BigDecimal celsius = converter.extractTemperatureAsCelsius(temperature);

        assertEquals(new BigDecimal("33.80"), celsius);
    }

    @Test
    void shouldExtractFahrenheit() {
        Temperature temperature = new TemperatureMeasurement(null, null, new BigDecimal("50.00"), null, TemperatureUnit.FAHRENHEIT);
        BigDecimal celsius = converter.extractTemperatureAsCelsius(temperature);

        assertEquals(new BigDecimal("10.00"), celsius);
    }

    //more data

}