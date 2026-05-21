package com.banreservas.customer.infrastructure.persistence;

import com.banreservas.customer.domain.exception.CustomerNotFoundException;
import com.banreservas.customer.domain.model.Customer;
import com.banreservas.customer.domain.model.PagedResult;
import com.banreservas.customer.domain.port.CustomerRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerJpaRepository implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        if (customer.id() == null) {
            CustomerJpaEntity entity = CustomerMapper.toNewEntity(customer);
            entity.persistAndFlush();
            return CustomerMapper.toDomain(entity);
        }
        CustomerJpaEntity entity = CustomerJpaEntity
            .<CustomerJpaEntity>findByIdOptional(customer.id())
            .orElseThrow(() -> new CustomerNotFoundException(customer.id()));
        CustomerMapper.applyUpdates(entity, customer);
        return CustomerMapper.toDomain(entity);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return CustomerJpaEntity.<CustomerJpaEntity>findByIdOptional(id)
            .map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return CustomerJpaEntity.<CustomerJpaEntity>listAll()
            .stream().map(CustomerMapper::toDomain).toList();
    }

    @Override
    public List<Customer> findByCountry(String country) {
        return CustomerJpaEntity.<CustomerJpaEntity>list("country", country)
            .stream().map(CustomerMapper::toDomain).toList();
    }

    @Override
    public PagedResult<Customer> findAllPaged(int page, int size, String country) {
        PanacheQuery<CustomerJpaEntity> query = (country != null)
            ? CustomerJpaEntity.find("country", country)
            : CustomerJpaEntity.findAll();

        long total = query.count();
        List<Customer> data = query.page(page, size).list()
            .stream().map(CustomerMapper::toDomain).toList();
        int totalPages = (int) Math.ceil((double) total / size);

        return new PagedResult<>(data, page, size, total, totalPages);
    }

    @Override
    public boolean existsByEmail(String email) {
        return CustomerJpaEntity.count("email", email) > 0;
    }

    @Override
    public void deleteById(Long id) {
        CustomerJpaEntity.deleteById(id);
    }
}
