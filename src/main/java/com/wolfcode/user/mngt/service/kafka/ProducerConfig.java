package com.wolfcode.user.mngt.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerConfig {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendUserDeactivatePayload(String email){


        kafkaTemplate.send("user-deactivate-topic", email);
    }

}
