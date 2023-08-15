package com.windsor.mockbank.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final TransactionService transactionService;

    @KafkaListener(topics = "transaction", groupId = "mock-bank")
    public void consumeTransaction(String transactionJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);

        transactionService.createTransaction(transaction);
    }
}

