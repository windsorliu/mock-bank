package com.windsor.mockbank;

import com.windsor.mockbank.controller.DemoController;
import com.windsor.mockbank.dto.AccountRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.service.GenerateDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class Application {

    private volatile boolean running = true;
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private GenerateDataService generateDataService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Scheduled(fixedRate = 5000) // Call User API every 5 seconds
    public void callUserApi() {
        if (running) {
            //Call User API and generate a user

            User userRequestBody = generateDataService.generateUser();
            ResponseEntity<User> user = callApi("http://localhost:8080/api/users",
                    userRequestBody, User.class);

            log.info("Generating user: {}", user.getBody().getUserKey());

            //Call Account API and generate accounts
            AccountRequest accountRequest = new AccountRequest();
            accountRequest.setAccountList(generateDataService.generateAccount(user.getBody().getUserId()));
            ResponseEntity<AccountRequest> accountList = callApi("http://localhost:8080/api/accounts",
                    accountRequest, AccountRequest.class);

            List<String> accountIBANList = accountList
                    .getBody()
                    .getAccountList()
                    .stream()
                    .map(Account::getAccountIBAN)
                    .collect(Collectors.toList());

            log.info("Generating Account IBAN: {}", accountIBANList);
        }
    }

    private <T> ResponseEntity<T> callApi(String url, Object requestBody, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                responseType
        );

        return response;
    }

    @Scheduled(fixedRate = 10000) // Call Transaction API every 10 seconds
    public void callTransactionApi() {
        if (running) {
            // TODO: Call Transaction API to perform a transaction
            System.out.println("Calling Transaction API to perform a transaction...");
        }
    }

    public Application() {
        // Start a separate thread to listen for user input
        new Thread(() -> {
            System.out.println("Press Enter to stop the program.");
            Scanner scanner = new Scanner(System.in);
            try {
                scanner.nextLine();
                running = false;
                System.out.println("Stopping the program...");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                scanner.close();
            }
        }).start();
    }
}
