package com.windsor.mockbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequest {

    private String uniqueId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
