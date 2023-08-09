package com.windsor.mockbank.util;

import com.windsor.mockbank.constant.Currency;
import com.windsor.mockbank.dto.AccountRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountDataGenerator {

    public static List<AccountRequest> generateAccount(Integer userId) {
        Random random = new Random();
        List<AccountRequest> accountRequestList = new ArrayList<>();

        int numOfAccounts = random.nextInt(3) + 1; // 每個user最少1個，最多3個帳戶

        for (int j = 0; j < numOfAccounts; j++) {
            AccountRequest accountRequest = new AccountRequest();
            accountRequest.setUserId(userId);

            Currency[] currencies = Currency.values();
            Currency randomCurrency = currencies[random.nextInt(currencies.length)];
            accountRequest.setCurrency(randomCurrency.name());

            accountRequest.setBalance(BigDecimal.valueOf(random.nextInt(1000000 - 100 + 1) + 100));  // (max - min + 1) + min

            accountRequestList.add(accountRequest);
        }

        return accountRequestList;
    }
}