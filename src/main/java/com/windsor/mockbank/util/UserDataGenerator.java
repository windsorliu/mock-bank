package com.windsor.mockbank.util;

import com.github.javafaker.Faker;
import com.windsor.mockbank.dto.UserRequest;
import com.windsor.mockbank.model.User;

import java.security.SecureRandom;
import java.util.Base64;

public class UserDataGenerator {

    public static UserRequest generateUser() {
        Faker faker = new Faker();
        UserRequest userRequest = new UserRequest();

        userRequest.setEmail(faker.internet().emailAddress());
        userRequest.setPassword(generateRandomPassword(12)); // 生成12個字符長的密碼

        return userRequest;
    }

    private static String generateRandomPassword(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}

