package com.windsor.mockbank.dao;

import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.rowmapper.AccountRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AccountDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final static Logger log = LoggerFactory.getLogger(AccountDao.class);

    public Account getAccountById(Integer accountId) {

        String sql = "SELECT account_id, account_IBAN, user_id, currency, " +
                "balance, created_date, last_modified_date " +
                "FROM account WHERE account_id = :account_id";

        Map<String, Object> map = new HashMap<>();
        map.put("account_id", accountId);

        List<Account> accountList = namedParameterJdbcTemplate.query(sql, map, new AccountRowMapper());

        if (accountList.isEmpty()) {
            log.warn("The account with account_id {} does not exist.", accountId);
            return null;
        }

        return accountList.get(0);
    }

    public List<Integer> getAccountIdList() {
        String sql = "SELECT account_id FROM account";

        Map<String, Object> map = new HashMap<>();

        List<Integer> AccountIdList = namedParameterJdbcTemplate.queryForList(sql, map, Integer.class);

        return AccountIdList;
    }

    public void updateBalance(BigDecimal balance, Integer accountId) {
        String sql = "UPDATE account SET balance = :balance " +
                "WHERE account_id = :account_id";

        Map<String, Object> map = new HashMap<>();
        map.put("balance", balance);
        map.put("account_id", accountId);

        namedParameterJdbcTemplate.update(sql, map);
    }
}
