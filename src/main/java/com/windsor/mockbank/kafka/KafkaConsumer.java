package com.windsor.mockbank.kafka;

import com.windsor.mockbank.model.Transaction;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "transaction", groupId = "mock-bank")
    public void consumeTransaction(String consumer) {
        // 在這裡處理收到的交易訊息
        System.out.println("Received transaction: " + consumer);
    }
}

