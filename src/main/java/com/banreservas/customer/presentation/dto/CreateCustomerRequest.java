package com.banreservas.customer.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateCustomerRequest {

    @NotBlank(message = "firstName is required")
    public String firstName;

    public String middleName;

    @NotBlank(message = "lastName is required")
    public String lastName;

    public String secondLastName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    public String email;

    @NotBlank(message = "address is required")
    public String address;

    @NotBlank(message = "phone is required")
    public String phone;

    @NotBlank(message = "country is required")
    @Size(min = 2, max = 2, message = "country must be exactly 2 characters")
    @Pattern(regexp = "[A-Za-z]{2}", message = "country must be a 2-letter ISO 3166 code")
    public String country;
}
