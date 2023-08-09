package com.windsor.mockbank.controller;

import com.windsor.mockbank.dto.UserRequest;
import com.windsor.mockbank.model.Account;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "The email has been registered",
                            responseCode = "409",
                            content = @Content
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody @Valid UserRequest userRequest) {
        Integer id = userService.signUp(userRequest);

        User userResponse = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Wrong password",
                            responseCode = "401",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "The email has not been registered yet",
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserRequest userRequest) {

        User userResponse = userService.login(userRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @Operation(
            summary = "Get all accounts from the specified user",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "The user does not exist",
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{userKey}/accounts")
    public ResponseEntity<List<Account>> getAccountsByUserKey(
            @Parameter(description = "Unique identifier of the user")
            @PathVariable String userKey) {

        List<Account> accountList = userService.getAccountsByUserKey(userKey);

        return ResponseEntity.status(HttpStatus.OK).body(accountList);
    }
}
