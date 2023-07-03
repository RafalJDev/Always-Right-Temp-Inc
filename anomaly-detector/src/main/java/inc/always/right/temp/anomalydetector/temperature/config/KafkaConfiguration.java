
package inc.always.right.temp.anomalydetector.temperature.config;

import inc.always.right.temp.anomalydetector.temperature.measurement.TemperatureMeasurement;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
class KafkaConfiguration {

    private static final String DEAD_LETTER = "temperature-measurements-dead-letter";
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    ProducerFactory<String, TemperatureMeasurement> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    ConsumerFactory<String, TemperatureMeasurement> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String,
            TemperatureMeasurement>
    listener() {
        ConcurrentKafkaListenerContainerFactory<
                String, TemperatureMeasurement> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    @Bean
    KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    NewTopic deadLetterTopic() {
        return TopicBuilder.name(DEAD_LETTER)
                .partitions(1)
                .build();
    }

    @Bean
    DefaultErrorHandler defaultErrorHandler(
            KafkaOperations<Object, Object> operations) {

        var recover = new DeadLetterPublishingRecoverer(operations,
                (cr, e) -> new TopicPartition(cr.topic() + DEAD_LETTER, 0));

        var exponentialBackOff = new ExponentialBackOffWithMaxRetries(5);

        var errorHandler = new DefaultErrorHandler(recover, exponentialBackOff);
        errorHandler.addNotRetryableExceptions(javax.validation.ValidationException.class);
        return errorHandler;
    }

}