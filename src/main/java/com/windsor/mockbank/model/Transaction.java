package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {

    @JsonProperty("transaction_id")
    private Integer transactionId;

    @JsonProperty("transaction_key")
    private String transactionKey;

    @JsonProperty("remitter_account_IBAN")
    private String remitterAccountIBAN;

    @JsonProperty("payee_account_IBAN")
    private String payeeAccountIBAN;

    private BigDecimal amount;
    private String currency;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;

    private String description;
}
