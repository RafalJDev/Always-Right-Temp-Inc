package inc.always.right.temp.anomalydetector.temperature;

import com.jupitertools.springtestredis.RedisTestContainer;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@RedisTestContainer(
        hostTargetProperty = "spring.data.redis.host",
        portTargetProperty = "spring.data.redis.port"
)
@Testcontainers
@ActiveProfiles("test")
public class BaseDatabaseIT {

    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgresql = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testcontainer")
            .withUsername("user")
            .withPassword("pass");

    @AfterAll
    static void teardown() {
        postgresql.stop();
    }

}
