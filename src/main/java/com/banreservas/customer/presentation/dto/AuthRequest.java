package com.banreservas.customer.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    @NotBlank(message = "username is required")
    public String username;

    @NotBlank(message = "password is required")
    public String password;
}
