package com.banreservas.customer.application.command;

public record CreateCustomerCommand(
    String firstName,
    String middleName,
    String lastName,
    String secondLastName,
    String email,
    String address,
    String phone,
    String country
) {}
