package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.dto.AccountRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final static Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountDao accountDao;
    private final UserDao userDao;

    public List<Account> createAccounts(List<AccountRequest> accountRequestList) {

        List<Account> accountList = new ArrayList<>();

        for (int i = 0; i < accountRequestList.size(); i++) {

            // 判斷是否有該用戶存在
            User user = userDao.getUserById(accountRequestList.get(i).getUserId());

            if (user == null) {
                log.warn("The user with user_id: {} does not exist", accountRequestList.get(i).getUserId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            String accountIBAN = UniqueIdentifierGenerator.generateAccountIBAN();

            Integer accountId = accountDao.createAccount(accountRequestList.get(i), accountIBAN);
            Account account = accountDao.getAccountById(accountId);
            accountList.add(account);
        }

        return accountList;
    }
}
