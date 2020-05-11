package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Role;
import com.app.domain.repository.RoleRepository;
import com.app.infrastructure.repository.jpa.JpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final JpaRoleRepository jpaRoleRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRoleRepository.findByName(name);
    }

    @Override
    public List<Role> findAllByNames(List<String> names) {
        return jpaRoleRepository.findAllByNameIn(names);
    }

    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll();
    }

    @Override
    public Optional<Role> findOne(Long id) {
        return jpaRoleRepository.findById(id);
    }

    @Override
    public Role save(Role role) {
        return jpaRoleRepository.save(role);
    }
}
