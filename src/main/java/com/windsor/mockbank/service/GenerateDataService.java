package com.windsor.mockbank.service;

import com.github.javafaker.Faker;
import com.windsor.mockbank.dao.GenerateDataDao;
import com.windsor.mockbank.dto.UserRegisterRequest;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GenerateDataService {

    @Autowired
    private GenerateDataDao generateDataDao;

    public List<UserRegisterRequest> generateUsers(int numberOfUsers) {
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

    private String generateRandomPassword(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void createUserData(List<UserRegisterRequest> userRegisterRequestList) {
        generateDataDao.createUserData(userRegisterRequestList);
    }
}

