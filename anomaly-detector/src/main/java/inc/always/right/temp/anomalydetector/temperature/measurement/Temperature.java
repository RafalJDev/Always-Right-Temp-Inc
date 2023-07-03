package inc.always.right.temp.anomalydetector.temperature.measurement;

import java.math.BigDecimal;

public interface Temperature {

    BigDecimal temperature();
    TemperatureUnit temperatureUnit();
}
