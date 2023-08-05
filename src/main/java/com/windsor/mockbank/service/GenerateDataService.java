package com.windsor.mockbank.service;

import com.github.javafaker.Faker;
import com.windsor.mockbank.dao.GenerateDataDao;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.constant.Currency;
import com.windsor.mockbank.util.JwtTokenGenerator;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

@Service
public class GenerateDataService {

    public User generateUser() {

        Faker faker = new Faker();
        User user = new User();

        String userKey = UniqueIdentifierGenerator.generateUserKey();

        user.setUserKey(userKey);
        user.setToken(JwtTokenGenerator.generateJwtToken(userKey));
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(generateRandomPassword(12)); // 生成12個字符長的密碼

        return user;
    }

    private String generateRandomPassword(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public List<Account> generateAccount(Integer userId) {
        Random random = new Random();
        List<Account> accountList = new ArrayList<>();

        int numOfAccounts = random.nextInt(3) + 1; // 每個user最少1個，最多3個帳戶

        for (int j = 0; j < numOfAccounts; j++) {
            Account account = new Account();
            account.setAccountIBAN("IBAN-" + UUID.randomUUID().toString());
            account.setUserId(userId);

            Currency[] currencies = Currency.values();
            Currency randomCurrency = currencies[random.nextInt(currencies.length)];
            account.setCurrency(randomCurrency.name());

            account.setBalance(BigDecimal.valueOf(random.nextInt(10000000 - 100 + 1) + 100));  // (max - min + 1) + min

            accountList.add(account);
        }

        return accountList;
    }
}

