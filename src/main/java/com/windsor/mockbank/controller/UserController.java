package com.windsor.mockbank.controller;

import com.windsor.mockbank.model.User;
import com.windsor.mockbank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<User> register(@RequestBody User user) {

        Integer id = userService.register(user);

        User userResponse = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }


}
