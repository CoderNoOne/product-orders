package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByAddress(String address);
}
