package inc.always.right.temp.anomalydetector.temperature.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MapperConfig {

    @Bean
    ObjectMapper objectMapper() {
        return  new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
