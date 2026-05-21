package com.banreservas.customer.infrastructure.persistence;

import com.banreservas.customer.domain.model.Customer;

public class CustomerMapper {

    private CustomerMapper() {}

    public static Customer toDomain(CustomerJpaEntity entity) {
        return new Customer(
            entity.id,
            entity.firstName,
            entity.middleName,
            entity.lastName,
            entity.secondLastName,
            entity.email,
            entity.address,
            entity.phone,
            entity.country,
            entity.demonym,
            entity.createdAt,
            entity.updatedAt
        );
    }

    public static CustomerJpaEntity toNewEntity(Customer customer) {
        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.firstName = customer.firstName();
        entity.middleName = customer.middleName();
        entity.lastName = customer.lastName();
        entity.secondLastName = customer.secondLastName();
        entity.email = customer.email();
        entity.address = customer.address();
        entity.phone = customer.phone();
        entity.country = customer.country();
        entity.demonym = customer.demonym();
        return entity;
    }

    public static void applyUpdates(CustomerJpaEntity entity, Customer customer) {
        entity.email = customer.email();
        entity.address = customer.address();
        entity.phone = customer.phone();
        entity.country = customer.country();
        entity.demonym = customer.demonym();
    }
}
