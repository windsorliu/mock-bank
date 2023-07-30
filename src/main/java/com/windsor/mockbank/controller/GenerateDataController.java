package com.windsor.mockbank.controller;

import com.windsor.mockbank.service.GenerateDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 生成user，account資料
@RestController
@RequestMapping("/api/generate")
public class GenerateDataController {

    @Autowired
    private GenerateDataService generateDataService;

    @Value("${numberOfUsers}")
    private int numberOfUsers;

    @GetMapping("/user")
    public void generateUsers() {

        generateDataService.generateUsers(numberOfUsers);
    }

    @GetMapping("/account")
    public void generateAccountData() {

        generateDataService.generateAccounts();
    }
}
