package com.app.domain.repository;

import com.app.domain.entity.Producer;
import com.app.domain.generic.CrudRepository;
import com.app.infrastructure.dto.ProducerDto;

import java.util.List;
import java.util.Optional;

public interface ProducerRepository extends CrudRepository<Producer, Long> {
    Optional<Producer> findByName(String name);
    Optional<Producer> findByNameWithFetchedGuarantees(String name);

    List<Producer> findAllByTrade(String trade);
}
