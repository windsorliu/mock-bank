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

    @JsonIgnore
    private Integer transactionId;

    @JsonProperty("transaction_key")
    private String transactionKey;

    @JsonProperty("remitter_user_id")
    private Integer remitterUserId;

    @JsonProperty("remitter_account_id")
    private Integer remitterAccountId;

    @JsonProperty("remitter_amount")
    private BigDecimal remitterAmount;

    @JsonProperty("remitter_currency")
    private String remitterCurrency;

    @JsonProperty("payee_user_id")
    private Integer payeeUserId;

    @JsonProperty("payee_account_id")
    private Integer payeeAccountId;

    @JsonProperty("payee_amount")
    private BigDecimal payeeAmount;

    @JsonProperty("payee_currency")
    private String payeeCurrency;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;

    private String description;
}
