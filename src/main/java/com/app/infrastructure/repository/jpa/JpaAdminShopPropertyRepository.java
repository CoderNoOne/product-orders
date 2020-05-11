package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.AdminShopProperty;
import com.app.domain.enums.AdminShopPropertyName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAdminShopPropertyRepository extends JpaRepository<AdminShopProperty, Long> {

    Optional<AdminShopProperty> findByProperty(AdminShopPropertyName property);
}
