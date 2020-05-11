package com.app.domain.repository;

import com.app.domain.entity.Manager;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface ManagerRepository extends CrudRepository<Manager, Long> {

    Optional<Manager> findOneWithLeastCustomers();
}
