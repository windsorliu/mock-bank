package com.windsor.mockbank.controller;

import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/exchangerate")
public class ExchangeRateController {

    @Value("${apiKey}")
    private String apiKey;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/update")
    public ResponseEntity<ExchangeRate> updateExchangeRate() {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

        ExchangeRate exchangeRate = restTemplate.getForObject(url, ExchangeRate.class);

        Integer id = exchangeRateService.createData(exchangeRate);

        return ResponseEntity.status(HttpStatus.OK)
                .body(exchangeRateService.getData(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeRate> getExchangeRateById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(exchangeRateService.getData(id));
    }
}
