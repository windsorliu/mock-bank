package com.windsor.mockbank.util;

import com.github.javafaker.Faker;
import com.windsor.mockbank.dto.UserRegisterRequest;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UserDataGenerator {

    public static List<UserRegisterRequest> generateUsers(int numberOfUsers) {
        List<UserRegisterRequest> userRegisterRequestList = new ArrayList<>();
        Faker faker = new Faker();

        for (int i = 0; i < numberOfUsers; i++) {
            String uniqueId = UniqueIdentifierGenerator.generateUserKey();
            String email = faker.internet().emailAddress();
            String password = generateRandomPassword(12); // 生成12個字符長的密碼
            
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest(uniqueId, email, password);
            userRegisterRequestList.add(userRegisterRequest);
        }

        return userRegisterRequestList;
    }

    private static String generateRandomPassword(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

