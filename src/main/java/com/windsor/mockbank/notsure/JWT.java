package com.windsor.mockbank.notsure;

import com.windsor.mockbank.util.JwtTokenGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWT {

    @GetMapping("/jwt/{userKey}")
    public String getJWT(@PathVariable String userKey) {
        return JwtTokenGenerator.generateJwtToken(userKey);
    }
}
