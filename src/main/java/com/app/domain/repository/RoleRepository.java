package com.app.domain.repository;

import com.app.domain.entity.Role;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findAllByNames(List<String> names);
}
