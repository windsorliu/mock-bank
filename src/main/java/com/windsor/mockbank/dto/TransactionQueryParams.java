package com.windsor.mockbank.dto;

import lombok.Data;

@Data
public class TransactionQueryParams {

    private String accountIBAN;
    private String orderBy;
    private String sort;
    private Integer limit;
    private Integer offset;
}
