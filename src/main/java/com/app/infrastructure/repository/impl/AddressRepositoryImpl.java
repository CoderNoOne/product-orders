package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Address;
import com.app.domain.repository.AddressRepository;
import com.app.infrastructure.repository.jpa.JpaAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class AddressRepositoryImpl implements AddressRepository {

    private final JpaAddressRepository jpaAddressRepository;

    @Override
    public List<Address> findAll() {
        return jpaAddressRepository.findAll();
    }

    @Override
    public Optional<Address> findOne(Long id) {
        return jpaAddressRepository.findById(id);
    }

    @Override
    public Address save(Address address) {
        return jpaAddressRepository.save(address);
    }

    @Override
    public Optional<Address> findByAddress(String address) {
        return jpaAddressRepository.findByAddress(address);
    }
}
