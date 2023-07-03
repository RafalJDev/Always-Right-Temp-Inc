package inc.always.right.temp.anomalydetector.temperature.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
class ExecutorConfig {

    @Bean
    ExecutorService executor() {
        return Executors.newFixedThreadPool(40);
    }
}
