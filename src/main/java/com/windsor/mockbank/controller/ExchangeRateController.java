package com.windsor.mockbank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.service.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/exchangerate")
public class ExchangeRateController {

    private final static Logger log = LoggerFactory.getLogger(ExchangeRateController.class);

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/update")
    public ResponseEntity<ExchangeRate> updateExchangeRate() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";
        ExchangeRate exchangeRate = restTemplate.getForObject(url, ExchangeRate.class);

        if(!exchangeRate.getResult().equals("success")) {
            log.warn("Exchange rate update failed.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Integer id = exchangeRateService.createData(exchangeRate);
        ExchangeRate response = exchangeRateService.getData(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/latest")
    public ExchangeRate getLatestData() {
        return exchangeRateService.getLatestData();
    }
}
