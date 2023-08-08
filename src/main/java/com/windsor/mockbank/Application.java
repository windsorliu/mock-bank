package com.windsor.mockbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.dto.AccountRequest;
import com.windsor.mockbank.dto.UserRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.AccountDataGenerator;
import com.windsor.mockbank.util.JwtTokenGenerator;
import com.windsor.mockbank.util.TransactionDataGenerator;
import com.windsor.mockbank.util.UserDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@RestController
@RequestMapping("/api/application")
public class Application {

    private final static Logger log = LoggerFactory.getLogger(Application.class);
    private volatile boolean running = false;

    @Autowired
    private TransactionDataGenerator transactionDataGenerator;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;

    @GetMapping("/stop")
    public void stop() {
        running = false;
        log.info("Stopping the transaction simulation program...");
    }

    @GetMapping("/start")
    public void start() {
        running = true;
        log.info("Starting the transaction simulation program...");
    }

    @Scheduled(fixedRate = 5000) // Call User API every 5 seconds
    public void callUserApi() {
        if (running) {
            RestTemplate restTemplate = new RestTemplate();

            //Call User API and generate a user
            UserRequest userRequest = UserDataGenerator.generateUser();
            User user = restTemplate.postForObject("http://localhost:8080/api/users/signup",
                    userRequest, User.class);

            //Call Account API and generate accounts
            AccountRequest accountRequest = AccountDataGenerator.generateAccount(user.getUserId());
            restTemplate.postForObject("http://localhost:8080/api/accounts",
                    accountRequest, Void.class);
        }
    }

    @Scheduled(fixedRate = 10000) // Call Transaction API every 10 seconds
    public void callTransactionApi() throws JsonProcessingException {
        if (running) {

            Transaction requestBody = transactionDataGenerator.generateTransaction();

            // 把 JWT token放入Header以驗證
            Account remitterAccount = accountDao.getAccountByIBAN(requestBody.getRemitterAccountIBAN());
            User remitterUser = userDao.getUserById(remitterAccount.getUserId());
            String url = "http://localhost:8080/api/transactions";
            String token = remitterUser.getToken();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity requestEntity = new HttpEntity(requestBody, headers);

            // call 交易API，驗證成功後就創建transaction
            int maxRetries = 3;
            int retryCount = 0;

            Transaction resultTransaction = null;

            for (; retryCount < maxRetries; retryCount++) {
                try {
                    ResponseEntity<Transaction> transaction = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            requestEntity,
                            Transaction.class
                    );

                    // 成功取得 response，保存 transaction 的內容
                    resultTransaction = transaction.getBody();
                    break; // 跳出迴圈，不再重試
                } catch (HttpClientErrorException.Unauthorized e) {
                    // 當遠端 API 回傳 401 Unauthorized 時的處理
                    // 更新token，準備進行重試
                    token = JwtTokenGenerator.generateJwtToken(remitterUser.getUserKey());
                    log.info("Generating a new token for user: {}", remitterUser.getUserKey());
                    HttpHeaders newHeader = new HttpHeaders();
                    newHeader.set("Authorization", token);
                    newHeader.setContentType(MediaType.APPLICATION_JSON);
                    requestEntity = new HttpEntity(requestBody, newHeader);
                } catch (Exception e) {
                    log.warn("Exception: {}", e.getMessage());
                    break;
                }
            }

            // 如果有新生成token，更新至mysql中
            if (retryCount != 0) {
                userDao.updateToken(remitterUser.getUserId(), token);
                log.info("The token for user: {} has been updated.", remitterUser.getUserKey());
            }

            if (resultTransaction == null) {
                log.warn("Transaction failed.");
            }
        }
    }

    @Scheduled(fixedRate = 900000/*3600000*/) // Call Transaction API every 1 hour
    public void updateExchangeRate() {
        if (running) {
            RestTemplate restTemplate = new RestTemplate();

            String url = "http://localhost:8080/api/exchangerate/update";

            restTemplate.getForObject(url, Void.class);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
