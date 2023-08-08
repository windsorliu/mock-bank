package com.windsor.mockbank.rowmapper;

import com.windsor.mockbank.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();

        transaction.setTransactionId(resultSet.getInt("transaction_id"));
        transaction.setTransactionKey(resultSet.getString("transaction_key"));
        transaction.setRemitterAccountIBAN(resultSet.getString("remitter_account_IBAN"));
        transaction.setPayeeAccountIBAN(resultSet.getString("payee_account_IBAN"));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setCurrency(resultSet.getString("currency"));
        transaction.setCreatedDate(resultSet.getTimestamp("created_date"));
        transaction.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));
        transaction.setDescription(resultSet.getString("description"));

        return transaction;
    }
}
