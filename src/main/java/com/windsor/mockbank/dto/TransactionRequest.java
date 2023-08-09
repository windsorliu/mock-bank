package com.windsor.mockbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @Schema(example = "IBAN-d8c46dcf-79e6-4f25-9797")
    @JsonProperty("remitter_account_IBAN")
    private String remitterAccountIBAN;

    @Schema(example = "IBAN-3cc9c52f-783c-4a99-4546")
    @JsonProperty("payee_account_IBAN")
    private String payeeAccountIBAN;

    @Schema(example = "1000")
    private BigDecimal amount;

    @Schema(example = "USD")
    private String currency;

    @Schema(example = "Online payment CHF")
    private String description;
}
