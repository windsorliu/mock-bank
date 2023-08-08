package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    private final static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private UserDao userDao;

    public List<Account> createAccounts(List<Account> accountList) {

        List<Account> accountResponseList = new ArrayList<>();

        for (int i = 0; i < accountList.size(); i++) {

            // 判斷是否有該用戶存在
            User user = userDao.getUserById(accountList.get(i).getUserId());

            if (user == null) {
                log.warn("The user with user_id: {} does not exist", accountList.get(i).getUserId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            accountList.get(i).setAccountIBAN(UniqueIdentifierGenerator.generateAccountIBAN());

            Integer accountId = accountDao.createAccount(accountList.get(i));
            Account account = accountDao.getAccountById(accountId);
            accountResponseList.add(account);
        }

        return accountResponseList;
    }
}
