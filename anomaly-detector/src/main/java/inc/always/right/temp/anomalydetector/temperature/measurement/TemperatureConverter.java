package inc.always.right.temp.anomalydetector.temperature.measurement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TemperatureConverter {

    private final int scale;

    public TemperatureConverter(@Value("${anomaly.detector.calculator.scale: 2}") int scale) {
        this.scale = scale;
    }

    private static final BigDecimal THIRTY_TWO = new BigDecimal("32.00");
    private static final BigDecimal FIVE = new BigDecimal("5.00");

    private static final BigDecimal NINE = new BigDecimal("9.00");

    public BigDecimal extractTemperatureAsCelsius(Temperature temperature) {
        if (TemperatureUnit.FAHRENHEIT.equals(temperature.temperatureUnit())) {
            return fahrenheitToCelsius(temperature.temperature());
        }
        return temperature.temperature();
    }

    public BigDecimal fahrenheitToCelsius(BigDecimal fahrenheit) {
        return fahrenheit.subtract(THIRTY_TWO)
                .multiply(FIVE)
                .divide(NINE, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal celsiusToFahrenheit(BigDecimal fahrenheit) {
        return fahrenheit.multiply(NINE)
                .divide(FIVE, 2, RoundingMode.HALF_UP)
                .add(THIRTY_TWO);
    }
}
