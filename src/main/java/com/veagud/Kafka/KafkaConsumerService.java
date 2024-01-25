package com.veagud.Kafka;

import com.veagud.model.Stair;
import com.veagud.service.OldClassCadScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    OldClassCadScript oldClassCadScript;

    @KafkaListener(topics = "myTopic", groupId = "my-group")
    public void listen(Stair stair) {
        oldClassCadScript.drawStair(stair);
    }
}
