package com.banreservas.customer.application.command;

public record UpdateCustomerCommand(
    String email,
    String address,
    String phone,
    String country
) {}
