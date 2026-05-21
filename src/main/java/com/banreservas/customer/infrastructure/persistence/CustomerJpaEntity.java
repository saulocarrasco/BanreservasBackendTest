package com.banreservas.customer.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
public class CustomerJpaEntity extends PanacheEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    public String firstName;

    @Column(name = "middle_name", length = 100)
    public String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    public String lastName;

    @Column(name = "second_last_name", length = 100)
    public String secondLastName;

    @Column(nullable = false, unique = true, length = 255)
    public String email;

    @Column(nullable = false, length = 500)
    public String address;

    @Column(nullable = false, length = 30)
    public String phone;

    @Column(nullable = false, length = 2)
    public String country;

    @Column(length = 100)
    public String demonym;

    @Version
    public Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;
}
