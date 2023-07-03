package inc.always.right.temp.anomaly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableR2dbcRepositories

public class AnomalyAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnomalyAnalyzerApplication.class, args);
	}

}
