package com.app.infrastructure.repository.impl;

import com.app.domain.entity.AdminShopProperty;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.infrastructure.repository.jpa.JpaAdminShopPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminShopPropertyRepositoryImpl implements AdminShopPropertyRepository {

    private final JpaAdminShopPropertyRepository jpaAdminShopPropertyRepository;

    @Override
    public List<AdminShopProperty> findAll() {
        return jpaAdminShopPropertyRepository.findAll();
    }

    @Override
    public Optional<AdminShopProperty> findOne(Long id) {
        return jpaAdminShopPropertyRepository.findById(id);
    }

    @Override
    public AdminShopProperty save(AdminShopProperty adminShopProperty) {
        return jpaAdminShopPropertyRepository.save(adminShopProperty);
    }

    @Override
    public Optional<AdminShopProperty> findByProperty(AdminShopPropertyName property) {
        return jpaAdminShopPropertyRepository.findByProperty(property);
    }

    @Override
    public void delete(AdminShopProperty adminShopProperty) {
        jpaAdminShopPropertyRepository.delete(adminShopProperty);
    }
}
