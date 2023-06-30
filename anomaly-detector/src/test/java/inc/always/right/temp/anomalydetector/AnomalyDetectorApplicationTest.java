package inc.always.right.temp.anomalydetector;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RedisTestContainer(
		hostTargetProperty = "spring.data.redis.host",
		portTargetProperty = "spring.data.redis.port"
)
@Testcontainers
class AnomalyDetectorApplicationTest {

	@Container
	@ServiceConnection
	static MySQLContainer mysql = new MySQLContainer<>("mysql:8.0.30")
			.withDatabaseName("testcontainer")
			.withUsername("user")
			.withPassword("pass");

	@Test
	void contextLoads() {

	}

}
