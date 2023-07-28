package com.windsor.mockbank.controller;

import com.windsor.mockbank.model.ExchangeRate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/exchangerate")
public class ExchangeRateController {

    @GetMapping("/{baseCode}")
    public ExchangeRate getRate(@PathVariable String baseCode) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://v6.exchangerate-api.com/v6/17087be03d3ba9d5e77dfec3/latest/" + baseCode.toUpperCase();

        ExchangeRate exchangeRate = restTemplate.getForObject(url, ExchangeRate.class);

        return exchangeRate;
    }
}
