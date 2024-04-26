package com.wolfcode.user.mngt.service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {


    @Bean
    public NewTopic newTopic(){
        return TopicBuilder
                .name("user-deactivate-topic")
                .build();
    }
}
