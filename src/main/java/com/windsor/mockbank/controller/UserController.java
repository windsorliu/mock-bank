package com.windsor.mockbank.controller;

import com.windsor.mockbank.dto.UserLoginRequest;
import com.windsor.mockbank.dto.UserRegisterRequest;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.service.UserService;
import com.windsor.mockbank.util.UserDataGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${numberOfUsers}")
    private int numberOfUsers;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        Integer id = userService.register(userRegisterRequest);

        User user = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        User user = userService.login(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/generate-data")
    public ResponseEntity<String> generateUserData() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.generateUserData(
                        UserDataGenerator.generateUsers(numberOfUsers)
                ));
    }
}
