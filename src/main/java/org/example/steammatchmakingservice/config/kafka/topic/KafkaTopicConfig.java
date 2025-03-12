package org.example.steammatchmakingservice.config.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.reply.matchmaking}")
    private String replyMatchmaking;

    @Value("${kafka.topic.request.matchmaking}")
    private String requestMatchmaking;
    @Value("${kafka.topic.request.notification}")
    private String requestNotification;


    @Bean
    public KafkaAdmin.NewTopics requests() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(requestMatchmaking).partitions(1).replicas(2).build(),
                TopicBuilder.name(requestMatchmaking).partitions(3).replicas(2).build());
    }

    @Bean
    public KafkaAdmin.NewTopics replies() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(replyMatchmaking).partitions(1).replicas(2).build());
    }
}
