package com.windsor.mockbank.notsure;

import com.windsor.mockbank.model.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet resultSet, int i) throws SQLException {
        Account account = new Account();

        account.setAccountId(resultSet.getInt("account_id"));
        account.setAccountIBAN(resultSet.getString("account_IBAN"));
        account.setUserId(resultSet.getInt("user_id"));
        account.setCurrency(resultSet.getString("currency"));
        account.setBalance(resultSet.getBigDecimal("balance"));
        account.setCreatedDate(resultSet.getTimestamp("created_date"));
        account.setLastModifiedDate(resultSet.getTimestamp("last_modified_date"));

        return account;
    }
}

