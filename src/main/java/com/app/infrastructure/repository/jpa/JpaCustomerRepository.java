package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCustomerRepository extends JpaRepository <Customer, Long> {

    Optional<Customer> findByUsername(String username);

}
