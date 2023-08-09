package com.windsor.mockbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank
    @Email
    @Schema(example = "test@gmail.com")
    private String email;

    @NotBlank
    @Schema(example = "Ywi9L8DhUnvU1npB")
    private String password;
}
