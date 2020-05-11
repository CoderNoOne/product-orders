package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Producer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProducerRepository extends JpaRepository<Producer, Long> {
    Optional<Producer> findByName(String name);

    @EntityGraph(attributePaths = "guarantees", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Producer> findByNameIs(String name);
}
