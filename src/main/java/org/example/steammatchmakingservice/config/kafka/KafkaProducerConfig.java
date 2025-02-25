package org.example.steammatchmakingservice.config.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerConfigString() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> producerConfigByte() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryString(@Qualifier("producerConfigString") Map producerConfigString) {
        return new DefaultKafkaProducerFactory<String, String>(producerConfigString);
    }

    @Bean
    public ProducerFactory<byte[], byte[]> producerFactoryByte(@Qualifier("producerConfigByte") Map producerConfigByte) {
        return new DefaultKafkaProducerFactory<byte[], byte[]>(producerConfigByte);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString(@Qualifier("producerFactoryString") ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public KafkaTemplate<byte[], byte[]> kafkaTemplateByte(@Qualifier("producerFactoryByte") ProducerFactory<byte[], byte[]> pf) {
        return new KafkaTemplate<>(pf);
    }
}
