package com.windsor.mockbank.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    @KafkaListener(topics = "test", groupId = "my-consumer-group")
    public void consumeTransaction(String message) {
        // 在這裡處理收到的交易訊息
        System.out.println("Received transaction: " + message);
    }
}

