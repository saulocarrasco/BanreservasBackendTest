package com.banreservas.customer.domain.model;

import java.time.LocalDateTime;

public record Customer(
    Long id,
    String firstName,
    String middleName,
    String lastName,
    String secondLastName,
    String email,
    String address,
    String phone,
    String country,
    String demonym,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
