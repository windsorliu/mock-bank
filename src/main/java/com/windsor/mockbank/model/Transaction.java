package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    @Schema(example = "1")
    @JsonProperty("transaction_id")
    private Integer transactionId;

    @Schema(description = "Unique identifier of the transaction",
            example = "2876b3ef-4add-4613-af59-e7ca5f1d1234")
    @JsonProperty("transaction_key")
    private String transactionKey;

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

    @JsonProperty("created_date")
    @Schema(example = "2023-08-09 18:28:20")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    @Schema(example = "2023-08-09 18:28:20")
    private Date lastModifiedDate;

    @Schema(example = "Online payment CHF")
    private String description;
}
