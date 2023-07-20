package com.windsor.mockbank.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
public class JwtController {

    @GetMapping("/{userId}")
    public String getJWT(@PathVariable String userId) {
        return JwtTokenGenerator.generateJwtToken(userId);
    }

    @GetMapping("/validate/{token}")
    public Jws<Claims> validate(@PathVariable String token) {
        return JwtTokenGenerator.validateJwtToken(token);
    }
}
