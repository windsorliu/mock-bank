package com.windsor.mockbank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Account {

    @JsonProperty("account_id")
    private Integer accountId;

    @JsonProperty("account_IBAN")
    private String accountIBAN;

    @JsonProperty("user_id")
    private Integer userId;

    private String currency;
    private BigDecimal balance;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("last_modified_date")
    private Date lastModifiedDate;
}
