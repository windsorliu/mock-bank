package com.windsor.mockbank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.windsor.mockbank.dao.AccountDao;
import com.windsor.mockbank.dao.ExchangeRateDao;
import com.windsor.mockbank.dao.GenerateDataDao;
import com.windsor.mockbank.dao.UserDao;
import com.windsor.mockbank.kafka.KafkaProducer;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.model.Transaction;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.util.JwtTokenGenerator;
import com.windsor.mockbank.util.UniqueIdentifierGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private GenerateDataDao generateDataDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private UserDao userDao;


    @Autowired
    private ExchangeRateDao exchangeRateDao;

    @Autowired
    private KafkaProducer kafkaProducer;
    private final static Logger log = LoggerFactory.getLogger(DemoController.class);

    @PostMapping("/transaction")
    public Transaction transaction(@RequestHeader(name = "Authorization") String authorization,
                                   @RequestBody Transaction requestBody) {

        if (authorization != null && authorization.startsWith("Bearer ")) {
            Jws<Claims> claims = JwtTokenGenerator
                    .validateJwtToken(authorization.substring(7));


            if (claims == null) {
                log.warn("Token for user_key: {} has expired.",
                        requestBody.getRemitterUserId());


                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }


        } else {
            // 沒有Authorization header或格式不正確，表示無效的token。
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return requestBody;
    }

    @GetMapping
    public void start() throws JsonProcessingException {

        // 選擇 匯款人(remitter) 和 收款人(payee) 帳戶
        List<Integer> AccountIdList = accountDao.getAccountIdList();
        Random random = new Random();
        Integer remitterAccountId;
        Integer payeeAccountId;

        while (true) {
            remitterAccountId = AccountIdList.get(random.nextInt(AccountIdList.size()));
            payeeAccountId = AccountIdList.get(random.nextInt(AccountIdList.size()));

            if (!remitterAccountId.equals(payeeAccountId)) {
                break;
            }
        }

        Account remitterAccount = accountDao.getAccountById(remitterAccountId);
        Account payeeAccount = accountDao.getAccountById(payeeAccountId);
        User remitterUser = userDao.getUserById(remitterAccount.getUserId());
        User payeeUser = userDao.getUserById(payeeAccount.getUserId());

        // 選擇 匯款人(remitter) 轉帳金額
        BigDecimal balance = remitterAccount.getBalance();

        double randomValue = random.nextDouble() * balance.doubleValue();
        BigDecimal remitterAmount = BigDecimal.valueOf(randomValue);

        if (remitterAccount.getBalance().compareTo(BigDecimal.ZERO) == 0 ||
                remitterAmount.compareTo(balance) > 0) {
            log.warn("The account with account_IBAN: {} " +
                            "does not have sufficient balance for the transaction."
                    , remitterAccount.getAccountIBAN());

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        balance = balance.subtract(remitterAmount);
        accountDao.updateBalance(balance, remitterAccountId);

        // call 匯率API
        ExchangeRate exchangeRate = exchangeRateDao.getLatestData();
        Map<String, Object> conversionRates = exchangeRate.getConversionRates();
        String remitterCurrency = remitterAccount.getCurrency();
        String payeeCurrency = payeeAccount.getCurrency();

        Number remitterNumber = (Number) conversionRates.get(remitterCurrency);
        Number payeeNumber = (Number) conversionRates.get(payeeCurrency);

        BigDecimal remitterRate = new BigDecimal(remitterNumber.toString());
        BigDecimal payeeRate = new BigDecimal(payeeNumber.toString());

        BigDecimal payeeAmount = remitterAmount.multiply(payeeRate).divide(remitterRate, 2, BigDecimal.ROUND_HALF_UP);

        // 創建transaction資訊
        Transaction requestBody = new Transaction();
        requestBody.setTransactionKey(UniqueIdentifierGenerator.generateTransactionKey());
        requestBody.setRemitterUserId(remitterUser.getUserId());
        requestBody.setRemitterAccountId(remitterAccountId);
        requestBody.setRemitterAmount(remitterAmount);
        requestBody.setRemitterCurrency(remitterCurrency);
        requestBody.setPayeeUserId(remitterUser.getUserId());
        requestBody.setPayeeAccountId(payeeAccountId);
        requestBody.setPayeeAmount(payeeAmount);
        requestBody.setPayeeCurrency(payeeCurrency);

        // 把 JWT token放入Header以驗證
        String url = "http://localhost:8080/api/demo/transaction";
        String token = remitterUser.getToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", token);
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity(requestBody, header);

        // call 交易API，驗證成功後就創建transaction
        int maxRetries = 5;
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
                log.info("生成新token");
                HttpHeaders newHeader = new HttpHeaders();
                newHeader.set("Authorization", token);
                newHeader.setContentType(MediaType.APPLICATION_JSON);
                requestEntity = new HttpEntity(requestBody, newHeader);

            } catch (HttpClientErrorException e) {
                // 其他 4xx 系列的錯誤處理
                log.warn("HttpClientErrorException: {}", e.getMessage());
            } catch (Exception e) {
                log.warn("Exception: {}", e.getMessage());
            }
        }

        if (retryCount != 0) {
            userDao.updateToken(remitterUser.getUserId(), token);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        if (resultTransaction != null) {
            String transactionJson = objectMapper.writeValueAsString(resultTransaction);
            kafkaProducer.sendTransaction(transactionJson);
//            return resultTransaction;
        } else {
            log.warn("Transaction failed");
//            return null;
        }
    }
}
