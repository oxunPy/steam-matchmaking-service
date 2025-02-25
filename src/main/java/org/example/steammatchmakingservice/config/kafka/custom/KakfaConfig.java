package org.example.steammatchmakingservice.config.kafka.custom;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KakfaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, MatchmakingRequest> producerFactoryMatchmaking() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MatchmakingRequestSerializer.class);
        return new DefaultKafkaProducerFactory<String, MatchmakingRequest>(props);
    }

    @Bean
    public KafkaTemplate<String, MatchmakingRequest> kafkaTemplateMatchmaking(@Qualifier("producerFactoryMatchmaking") ProducerFactory<String, MatchmakingRequest> pf) {
        return new KafkaTemplate<>(pf);
    }
}
