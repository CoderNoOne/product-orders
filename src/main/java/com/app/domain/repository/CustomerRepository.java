package com.app.domain.repository;

import com.app.domain.entity.Customer;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
}
