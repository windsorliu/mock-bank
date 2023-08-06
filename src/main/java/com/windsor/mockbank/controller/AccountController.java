package com.windsor.mockbank.controller;

import com.windsor.mockbank.dto.AccountRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountRequest> createAccounts(@RequestBody List<Account> accountList) {
        AccountRequest accountRequest = new AccountRequest();
        List<Account> accountResponseList = accountService.createAccounts(accountList);

        accountRequest.setAccountList(accountResponseList);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountRequest);
    }
}
