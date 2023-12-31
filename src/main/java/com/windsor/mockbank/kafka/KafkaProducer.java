package com.windsor.mockbank.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsor.mockbank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String TOPIC = "transaction";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendTransaction(Transaction transaction) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String transactionJson = objectMapper.writeValueAsString(transaction);

        kafkaTemplate.send(TOPIC, transaction.getTransactionKey(), transactionJson);
    }
}

