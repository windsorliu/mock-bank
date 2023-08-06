package com.windsor.mockbank.service;

import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountDao accountDao;

    public List<Account> createAccounts(List<Account> accountList) {

        List<Account> accountResponseList = new ArrayList<>();

        for (int i = 0; i < accountList.size(); i++) {
            accountList.get(i).setAccountIBAN(UniqueIdentifierGenerator.generateAccountIBAN());

            Integer accountId = accountDao.createAccount(accountList.get(i));
            Account account = accountDao.getAccountById(accountId);
            accountResponseList.add(account);
        }

        return accountResponseList;
    }
}
