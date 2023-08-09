package com.windsor.mockbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    @Schema(example = "1")
    @JsonProperty("user_id")
    private Integer userId;

    @Schema(example = "USD")
    private String currency;

    @Schema(example = "1000")
    private BigDecimal balance;
}