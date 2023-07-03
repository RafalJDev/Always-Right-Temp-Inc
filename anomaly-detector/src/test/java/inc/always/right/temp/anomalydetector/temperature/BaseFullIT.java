package inc.always.right.temp.anomalydetector.temperature;

import com.jupitertools.springtestredis.RedisTestContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
        topics = {"temperature-measurements"}
)
@Testcontainers
@RedisTestContainer(
        hostTargetProperty = "spring.data.redis.host",
        portTargetProperty = "spring.data.redis.port"
)
@ActiveProfiles("test")
public class BaseFullIT {

    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgresql = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withDatabaseName("testcontainer")
            .withUsername("user")
            .withPassword("pass");

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

}
