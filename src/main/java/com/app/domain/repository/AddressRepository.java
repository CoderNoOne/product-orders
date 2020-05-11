package com.app.domain.repository;

import com.app.domain.entity.Address;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface AddressRepository extends CrudRepository<Address, Long> {
    Optional<Address> findByAddress(String address);
}
