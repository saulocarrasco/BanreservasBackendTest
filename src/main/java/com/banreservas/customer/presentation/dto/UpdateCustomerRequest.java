package com.banreservas.customer.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateCustomerRequest {

    @Email(message = "email must be a valid email address")
    public String email;

    @Size(min = 1, message = "address must not be blank if provided")
    public String address;

    @Size(min = 1, message = "phone must not be blank if provided")
    public String phone;

    @Size(min = 2, max = 2, message = "country must be exactly 2 characters")
    @Pattern(regexp = "[A-Za-z]{2}", message = "country must be a 2-letter ISO 3166 code")
    public String country;
}
