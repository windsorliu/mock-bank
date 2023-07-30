package com.windsor.mockbank.service;

import com.github.javafaker.Faker;
import com.windsor.mockbank.dao.GenerateDataDao;
import com.windsor.mockbank.dto.UserRegisterRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.constant.Currency;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

@Service
public class GenerateDataService {

    @Autowired
    private GenerateDataDao generateDataDao;

    public void generateUsers(int numberOfUsers) {
        List<UserRegisterRequest> userRegisterRequestList = new ArrayList<>();
        Faker faker = new Faker();

        for (int i = 0; i < numberOfUsers; i++) {
            String uniqueId = UniqueIdentifierGenerator.generateUserKey();
            String email = faker.internet().emailAddress();
            String password = generateRandomPassword(12); // 生成12個字符長的密碼

            UserRegisterRequest userRegisterRequest = new UserRegisterRequest(uniqueId, email, password);
            userRegisterRequestList.add(userRegisterRequest);
        }

        generateDataDao.generateUsers(userRegisterRequestList);
    }

    private String generateRandomPassword(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void generateAccounts() {
        List<Account> accountList = new ArrayList<>();
        Random random = new Random();
        List<Integer> userIdList = generateDataDao.getUserIdList();

        for (int i = 0; i < userIdList.size(); i++) {
            int numOfAccounts = random.nextInt(3) + 1; // 每個user最少1個，最多3個帳戶

            for (int j = 0; j < numOfAccounts; j++) {

                Account account = new Account();
                account.setAccountIBAN("IBAN-" + UUID.randomUUID().toString());
                account.setUserId(userIdList.get(i));

                Currency[] currencies = Currency.values();
                Currency randomCurrency = currencies[random.nextInt(currencies.length)];
                account.setCurrency(randomCurrency.name());

                account.setBalance(BigDecimal.valueOf(random.nextInt(10000000 - 1000 + 1) + 1000));  // (max - min + 1) + min

                accountList.add(account);
            }
        }

        generateDataDao.generateAccounts(accountList);
    }
}

