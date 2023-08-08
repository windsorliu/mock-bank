package com.windsor.mockbank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.windsor.mockbank.dto.Page;
import com.windsor.mockbank.dto.TransactionQueryParams;
import com.windsor.mockbank.kafka.KafkaProducer;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.service.TransactionService;
import com.windsor.mockbank.util.JwtTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Validated
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

    @GetMapping("/{accountIBAN}")
    public ResponseEntity<Page<Transaction>> getTransactions(
            @PathVariable String accountIBAN,

            // 排序 Sorting
            @RequestParam(defaultValue = "created_date") String orderBy,
            @RequestParam(defaultValue = "desc") String sort,

            // 分頁 Pagination
            @RequestParam(defaultValue = "2") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset) {

        // 檢查 account_IBAN 是否存在
        transactionService.isAccountExist(accountIBAN);

        TransactionQueryParams transactionQueryParams = new TransactionQueryParams();
        transactionQueryParams.setAccountIBAN(accountIBAN);
        transactionQueryParams.setOrderBy(orderBy);
        transactionQueryParams.setSort(sort);
        transactionQueryParams.setLimit(limit);
        transactionQueryParams.setOffset(offset);

        // 取得 transaction list
        List<Transaction> transactionList = transactionService.getTransactions(transactionQueryParams);

        // 取得 transaction 總數
        Integer total = transactionService.countTransactions(accountIBAN);

        // 分頁
        Page<Transaction> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(transactionList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

}
