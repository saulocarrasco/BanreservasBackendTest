package com.banreservas.customer.application;

import com.banreservas.customer.application.command.CreateCustomerCommand;
import com.banreservas.customer.application.command.UpdateCustomerCommand;
import com.banreservas.customer.domain.exception.CustomerNotFoundException;
import com.banreservas.customer.domain.exception.DuplicateEmailException;
import com.banreservas.customer.domain.model.Customer;
import com.banreservas.customer.domain.model.PagedResult;
import com.banreservas.customer.domain.port.CountryGateway;
import com.banreservas.customer.domain.port.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CustomerService {

    @Inject
    CustomerRepository customerRepository;

    @Inject
    CountryGateway countryGateway;

    @Transactional
    public Customer create(CreateCustomerCommand cmd) {
        String email = cmd.email().trim().toLowerCase();
        String country = cmd.country().trim().toUpperCase();

        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String demonym = countryGateway.getDemonym(country);

        Customer customer = new Customer(
            null,
            cmd.firstName().trim(),
            cmd.middleName() != null ? cmd.middleName().trim() : null,
            cmd.lastName().trim(),
            cmd.secondLastName() != null ? cmd.secondLastName().trim() : null,
            email,
            cmd.address().trim(),
            cmd.phone().trim(),
            country,
            demonym,
            null,
            null
        );
        return customerRepository.save(customer);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findByCountry(String country) {
        return customerRepository.findByCountry(country.trim().toUpperCase());
    }

    public PagedResult<Customer> findAllPaged(int page, int size, String country) {
        String normalizedCountry = (country != null) ? country.trim().toUpperCase() : null;
        return customerRepository.findAllPaged(page, size, normalizedCountry);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Transactional
    public Customer update(Long id, UpdateCustomerCommand cmd) {
        Customer existing = findById(id);

        String newEmail = (cmd.email() != null) ? cmd.email().trim().toLowerCase() : null;
        String newCountry = (cmd.country() != null) ? cmd.country().trim().toUpperCase() : null;

        if (newEmail != null && !newEmail.equals(existing.email()) &&
                customerRepository.existsByEmail(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        String newDemonym = existing.demonym();
        if (newCountry != null && !newCountry.equals(existing.country())) {
            newDemonym = countryGateway.getDemonym(newCountry);
        }

        Customer updated = new Customer(
            existing.id(),
            existing.firstName(),
            existing.middleName(),
            existing.lastName(),
            existing.secondLastName(),
            newEmail != null ? newEmail : existing.email(),
            cmd.address() != null ? cmd.address().trim() : existing.address(),
            cmd.phone() != null ? cmd.phone().trim() : existing.phone(),
            newCountry != null ? newCountry : existing.country(),
            newDemonym,
            existing.createdAt(),
            existing.updatedAt()
        );
        return customerRepository.save(updated);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        customerRepository.deleteById(id);
    }
}
