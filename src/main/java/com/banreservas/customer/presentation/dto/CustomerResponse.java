package com.banreservas.customer.presentation.dto;

import com.banreservas.customer.domain.model.Customer;

import java.time.LocalDateTime;

public class CustomerResponse {

    public Long id;
    public String firstName;
    public String middleName;
    public String lastName;
    public String secondLastName;
    public String email;
    public String address;
    public String phone;
    public String country;
    public String demonym;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public static CustomerResponse from(Customer customer) {
        CustomerResponse r = new CustomerResponse();
        r.id = customer.id();
        r.firstName = customer.firstName();
        r.middleName = customer.middleName();
        r.lastName = customer.lastName();
        r.secondLastName = customer.secondLastName();
        r.email = customer.email();
        r.address = customer.address();
        r.phone = customer.phone();
        r.country = customer.country();
        r.demonym = customer.demonym();
        r.createdAt = customer.createdAt();
        r.updatedAt = customer.updatedAt();
        return r;
    }
}
