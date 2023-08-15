package com.windsor.mockbank.dao;

import com.windsor.mockbank.dto.TransactionQueryParams;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.rowmapper.TransactionRowMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TransactionDao {

    private final static Logger log = LoggerFactory.getLogger(TransactionDao.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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

    public List<Transaction> getTransactions(TransactionQueryParams transactionQueryParams) {
        String sql = "SELECT transaction_id, transaction_key, remitter_account_IBAN, " +
                "payee_account_IBAN, amount, currency, created_date, last_modified_date, description " +
                "FROM transaction WHERE 1 = 1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, transactionQueryParams.getAccountIBAN());

        // 排序  因為ORDER BY 子句需要的是欄位名稱，而不是值，所以不能預編譯
        sql = sql + " ORDER BY " + transactionQueryParams.getOrderBy() + " " + transactionQueryParams.getSort();

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";

        map.put("limit", transactionQueryParams.getLimit());
        map.put("offset", transactionQueryParams.getOffset());

        List<Transaction> transactionList = namedParameterJdbcTemplate.query(sql, map, new TransactionRowMapper());

        return transactionList;
    }

    public Integer countTransactions(String accountIBAN) {
        String sql = "SELECT count(*) FROM transaction WHERE 1 = 1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        sql = addFilteringSql(sql, map, accountIBAN);

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    private String addFilteringSql(String sql, Map<String, Object> map, String accountIBAN) {
        sql = sql + " AND remitter_account_IBAN = :remitter_account_IBAN " +
                "OR payee_account_IBAN = :payee_account_IBAN";

        map.put("remitter_account_IBAN", accountIBAN);
        map.put("payee_account_IBAN", accountIBAN);

        return sql;
    }
}
