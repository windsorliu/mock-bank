package com.windsor.mockbank.controller;

import com.windsor.mockbank.dto.UserRequest;
import com.windsor.mockbank.model.User;
import com.windsor.mockbank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody @Valid UserRequest userRequest) {
        Integer id = userService.signUp(userRequest);

        User userResponse = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserRequest userRequest) {

        User userResponse = userService.login(userRequest);

        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

}
