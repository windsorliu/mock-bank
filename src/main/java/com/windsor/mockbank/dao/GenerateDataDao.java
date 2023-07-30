package com.windsor.mockbank.dao;

import com.windsor.mockbank.model.User;
import com.windsor.mockbank.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GenerateDataDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final static Logger log = LoggerFactory.getLogger(GenerateDataDao.class);

    public void generateUsers(List<User> userList) {
        String sql = "INSERT INTO user(user_key, token, email, password) VALUE (:user_key, :token, :email, :password)";

        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[userList.size()];

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);

            parameterSources[i] = new MapSqlParameterSource();
            parameterSources[i].addValue("user_key", user.getUserKey());
            parameterSources[i].addValue("token", user.getToken());
            parameterSources[i].addValue("email", user.getEmail());
            parameterSources[i].addValue("password", user.getPassword());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);

        log.info("Successfully generated {} records of users data.", userList.size());
    }

    public List<Integer> getUserIdList() {
        String sql = "SELECT user_id FROM user";

        Map<String, Object> map = new HashMap<>();

        List<Integer> userIdList = namedParameterJdbcTemplate.queryForList(sql, map, Integer.class);

        return userIdList;
    }

    public void generateAccounts(List<Account> accountList) {
        String sql = "INSERT INTO account(account_IBAN, user_id, currency, balance) " +
                "VALUE (:account_IBAN, :user_id, :currency, :balance)";

        MapSqlParameterSource[] parameterSources = new MapSqlParameterSource[accountList.size()];

        for (int i = 0; i < accountList.size(); i++) {
            Account account = accountList.get(i);

            parameterSources[i] = new MapSqlParameterSource();
            parameterSources[i].addValue("account_IBAN", account.getAccountIBAN());
            parameterSources[i].addValue("user_id", account.getUserId());
            parameterSources[i].addValue("currency", account.getCurrency());
            parameterSources[i].addValue("balance", account.getBalance());
        }

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);

        log.info("Successfully generated {} records of accounts data.", accountList.size());
    }
}
