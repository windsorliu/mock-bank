package com.windsor.mockbank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.windsor.mockbank.model.ExchangeRate;
import com.windsor.mockbank.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/exchangerate")
@Tag(name = "Exchange Rate")
public class ExchangeRateController {

    private final static Logger log = LoggerFactory.getLogger(ExchangeRateController.class);

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Operation(
            summary = "Update exchange rates",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "The API key from the third-party provider is invalid or incorrect",
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "An unknown error occurred. Please contact the backend team for assistance",
                            responseCode = "400",
                            content = @Content
                    )
            }
    )
    @GetMapping("/update")
    public ResponseEntity<ExchangeRate> updateExchangeRate() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

        ExchangeRate exchangeRate;

        try {
            exchangeRate = restTemplate.getForObject(url, ExchangeRate.class);
        } catch (HttpClientErrorException.Forbidden e) {
            log.warn("The API key from the third-party provider is invalid or incorrect, Exchange rate update failed.");
            log.warn("HttpClientErrorException.Forbidden: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.warn("Exception: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Integer id = exchangeRateService.createData(exchangeRate);
        ExchangeRate response = exchangeRateService.getData(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get the latest exchange rate information",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "The exchange-rate table in the database is currently empty",
                            responseCode = "204",
                            content = @Content
                    )
            }
    )
    @GetMapping("/latest")
    public ResponseEntity<ExchangeRate> getLatestData() {
        ExchangeRate response = exchangeRateService.getLatestData();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
