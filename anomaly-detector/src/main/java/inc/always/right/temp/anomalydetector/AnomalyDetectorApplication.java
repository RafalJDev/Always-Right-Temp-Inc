package inc.always.right.temp.anomalydetector;

import inc.always.right.temp.anomalydetector.temperature.recent.redis.RecentTemperatureMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AnomalyDetectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnomalyDetectorApplication.class, args);
	}

}
