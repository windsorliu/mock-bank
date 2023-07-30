package com.windsor.mockbank.notsure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    private String userKey;

    private String token;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
