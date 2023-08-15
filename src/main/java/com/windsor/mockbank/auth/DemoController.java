package com.windsor.mockbank.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mock/demo")
public class DemoController {

    @GetMapping
    public String demo() {
        return "hello world";
    }
}
