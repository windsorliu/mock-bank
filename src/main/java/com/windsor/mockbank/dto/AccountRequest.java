package com.windsor.mockbank.dto;

import com.windsor.mockbank.model.Account;
import lombok.Data;

import java.util.List;

@Data
public class AccountRequest {

    private List<Account> accountList;
}
