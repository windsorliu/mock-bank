package com.windsor.mockbank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.windsor.mockbank.kafka.KafkaProducer;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.service.TransactionService;
import com.windsor.mockbank.util.JwtTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestHeader(name = "Authorization") String authorization,
                                                         @RequestBody Transaction transaction) throws JsonProcessingException {

        if (authorization != null && authorization.startsWith("Bearer ")) {
            Jws<Claims> claims = JwtTokenGenerator
                    .validateJwtToken(authorization.substring(7));

            if (claims == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        } else {
            // 沒有Authorization header或格式不正確，表示無效的token。
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String transactionKey = transactionService.getTransactionKey(transaction);
        transaction.setTransactionKey(transactionKey);
        kafkaProducer.sendTransaction(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

}
