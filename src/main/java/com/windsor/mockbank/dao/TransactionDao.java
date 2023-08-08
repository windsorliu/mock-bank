package com.windsor.mockbank.dao;

import com.windsor.mockbank.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TransactionDao {

    private final static Logger log = LoggerFactory.getLogger(TransactionDao.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transaction (transaction_key, remitter_account_IBAN, " +
                "payee_account_IBAN, amount, currency, description) " +
                "VALUES (:transaction_key, :remitter_account_IBAN, " +
                ":payee_account_IBAN, :amount, :currency, :description)";

        Map<String, Object> map = new HashMap<>();
        map.put("transaction_key", transaction.getTransactionKey());
        map.put("remitter_account_IBAN", transaction.getRemitterAccountIBAN());
        map.put("payee_account_IBAN", transaction.getPayeeAccountIBAN());
        map.put("amount", transaction.getAmount());
        map.put("currency", transaction.getCurrency());
        map.put("description", transaction.getDescription());

        namedParameterJdbcTemplate.update(sql, map);

        log.info("Creating transaction: {}", transaction.getTransactionKey());
    }
}
