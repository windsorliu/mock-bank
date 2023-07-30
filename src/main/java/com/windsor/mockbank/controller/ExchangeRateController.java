package com.windsor.mockbank.controller;

import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public ExchangeRate updateExchangeRate() {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

        ExchangeRate exchangeRate = restTemplate.getForObject(url, ExchangeRate.class);

        Integer id = exchangeRateService.createData(exchangeRate);

        //如果沒有返回，代表更新失敗
        return exchangeRateService.getData(id);
    }

    @GetMapping("/{id}")
    public ExchangeRate getExchangeRateById(@PathVariable Integer id) {
        return exchangeRateService.getData(id);
    }
}
