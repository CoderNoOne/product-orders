package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Customer;
import com.app.domain.repository.CustomerRepository;
import com.app.infrastructure.repository.jpa.JpaCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpaCustomerRepository;

    @Override
    public List<Customer> findAll() {
        return jpaCustomerRepository.findAll();
    }

    @Override
    public Optional<Customer> findOne(Long id) {
        return jpaCustomerRepository.findById(id);
    }

    @Override
    public Customer save(Customer customer) {
        return jpaCustomerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        return jpaCustomerRepository.findByUsername(username);
    }
}
