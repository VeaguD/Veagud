package com.veagud.Kafka;

import com.veagud.model.Stair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//@Service
public class KafkaProducerService {

//    @Autowired
    private KafkaTemplate<String, Stair> kafkaTemplate;

    public void sendMessage(String topic, Stair stair) {
        kafkaTemplate.send(topic, stair);
    }
}
