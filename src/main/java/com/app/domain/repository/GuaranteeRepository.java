package com.app.domain.repository;

import com.app.domain.entity.Guarantee;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface GuaranteeRepository extends CrudRepository<Guarantee, Long> {
    Optional<Guarantee> findByName(String guaranteeName);

    Set<Guarantee> saveAll(Set<Guarantee> guarantees);
}
