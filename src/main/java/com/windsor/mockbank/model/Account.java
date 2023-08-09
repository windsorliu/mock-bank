package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Account {

    @JsonProperty("account_id")
    @Schema(example = "1")
    private Integer accountId;

    @JsonProperty("account_IBAN")
    @Schema(example = "IBAN-4e90eee2-37c8-4687-1234")
    private String accountIBAN;

    @JsonProperty("user_id")
    @Schema(example = "1")
    private Integer userId;

    @Schema(example = "USD")
    private String currency;

    @Schema(example = "1000")
    private BigDecimal balance;

    @JsonProperty("created_date")
    @Schema(example = "2023-08-09 18:28:20")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    @Schema(example = "2023-08-09 18:28:20")
    private Date lastModifiedDate;
}
