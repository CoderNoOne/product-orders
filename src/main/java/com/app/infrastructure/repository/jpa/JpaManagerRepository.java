package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaManagerRepository extends JpaRepository<Manager, Long> {

    @Query("select m from Manager m order by m.customers.size desc")
    List<Manager> findManagersOrderByCustomersNumber();
}
