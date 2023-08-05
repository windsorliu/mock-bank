package com.windsor.mockbank.controller;

import com.windsor.mockbank.util.JwtTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

    @GetMapping("/{userId}")
    public String getJWT(@PathVariable String userId) {
        return JwtTokenGenerator.generateJwtToken(userId);
    }

    @GetMapping("/validate")
    public Jws<Claims> validate(@RequestHeader(name = "Authorization")
                                String authorization) {

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return JwtTokenGenerator.validateJwtToken(
                    authorization.substring(7));
        } else {
            return null; // 沒有Authorization header或格式不正確，返回null表示無效的token。
        }
    }
}
