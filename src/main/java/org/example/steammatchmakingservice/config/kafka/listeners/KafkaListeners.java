package org.example.steammatchmakingservice.config.kafka.listeners;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @Autowired
    private ObjectMapper objMapper;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateString;
}
