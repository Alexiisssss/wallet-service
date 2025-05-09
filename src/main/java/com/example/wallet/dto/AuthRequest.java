package com.example.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    private String email;

    private String phone;

    @NotBlank
    private String password;
}
