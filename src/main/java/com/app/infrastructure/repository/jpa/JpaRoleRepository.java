package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByNameIn(List<String> names);
    Optional<Role> findByName(String name);
}
