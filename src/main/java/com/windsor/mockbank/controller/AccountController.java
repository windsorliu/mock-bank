package com.windsor.mockbank.controller;

import com.windsor.mockbank.dto.AccountRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Account")
@RequiredArgsConstructor
public class AccountController {


    private final AccountService accountService;

    @Operation(
            summary = "Create one or more accounts",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "The user does not exist",
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<List<Account>> createAccounts(@RequestBody List<AccountRequest> accountRequestList) {

        List<Account> accountList = accountService.createAccounts(accountRequestList);

        return ResponseEntity.status(HttpStatus.CREATED).body(accountList);
    }
}
