package com.banreservas.customer.domain.port;

import com.banreservas.customer.domain.model.Customer;
import com.banreservas.customer.domain.model.PagedResult;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    List<Customer> findAll();
    List<Customer> findByCountry(String country);
    PagedResult<Customer> findAllPaged(int page, int size, String country);
    boolean existsByEmail(String email);
    void deleteById(Long id);
}
