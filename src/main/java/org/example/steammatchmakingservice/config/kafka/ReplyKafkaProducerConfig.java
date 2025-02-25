package org.example.steammatchmakingservice.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;
import java.util.Map;

@Configuration
public class ReplyKafkaProducerConfig {
    @Value("${kafka.consumer.matchmaking.group_id}")
    private String groupId;


    @Bean
    public ConsumerFactory<String, String> replyConsumerFactory(@Qualifier("consumerConfigString") Map consumerConfig) {
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return new DefaultKafkaConsumerFactory<String, String>(consumerConfig);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate(@Qualifier("producerFactoryString") ProducerFactory<String, String> pf,
                                                                            @Qualifier("repliesContainer")ConcurrentMessageListenerContainer<String, String> replyContainer) {
        ReplyingKafkaTemplate<String, String, String> kafkaTemplate = new ReplyingKafkaTemplate<>(pf, replyContainer);
        kafkaTemplate.setDefaultReplyTimeout(Duration.ofSeconds(3600));
        return kafkaTemplate;
    }
}
