package com.app.domain.repository;

import com.app.domain.entity.AdminShopProperty;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.generic.CrudRepository;

import java.util.Optional;

public interface AdminShopPropertyRepository extends CrudRepository<AdminShopProperty, Long> {
    Optional<AdminShopProperty> findByProperty(AdminShopPropertyName property);

    void delete(AdminShopProperty adminShopProperty);
}
