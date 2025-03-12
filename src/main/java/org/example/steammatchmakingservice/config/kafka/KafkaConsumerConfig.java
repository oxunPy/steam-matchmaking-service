package org.example.steammatchmakingservice.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.game.NoteData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;
    @Value("${kafka.consumer.matchmaking.group_id}")
    private String groupId;

    @Bean
    public Map<String, Object> consumerConfigString() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigByte() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigJsonDeserializer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put("spring.json.trusted.packages", "*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactoryString(@Qualifier("consumerConfigString") Map consumerStringConfig) {
        return new DefaultKafkaConsumerFactory<String, String>(consumerStringConfig);
    }

    @Bean
    public ConsumerFactory<byte[], byte[]> consumerFactoryByte(@Qualifier("consumerConfigByte") Map consumerByteConfig) {
        return new DefaultKafkaConsumerFactory<byte[], byte[]>(consumerByteConfig);
    }

    @Bean
    public ConsumerFactory<String, MatchmakingRequestDto> consumerFactoryMatchmakingRequest(@Qualifier("consumerConfigJsonDeserializer") Map consumerConfig) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

    @Bean
    public ConsumerFactory<String, NoteData> consumerFactoryNoteData(@Qualifier("consumerConfigJsonDeserializer") Map consumerConfig) {
        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactoryString(@Qualifier("consumerFactoryString") ConsumerFactory<String, String> cf) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<byte[], byte[]>> kafkaListenerContainerFactoryByte(@Qualifier("consumerFactoryByte") ConsumerFactory<byte[], byte[]> cf) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MatchmakingRequestDto>> kafkaListenerContainerFactoryMatchmakingRequest(@Qualifier("consumerFactoryMatchmakingRequest") ConsumerFactory<String, MatchmakingRequestDto> cf) {
        ConcurrentKafkaListenerContainerFactory<String, MatchmakingRequestDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, NoteData>> kafkaListenerContainerFactoryNoteData(@Qualifier("consumerFactoryNoteData") ConsumerFactory<String, NoteData> cf) {
        ConcurrentKafkaListenerContainerFactory<String, NoteData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }
}
