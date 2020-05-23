package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ReservedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaReservedProductRepository extends JpaRepository<ReservedProduct, Long> {
    void deleteByProductOrderId(Long id);

    List<ReservedProduct> findAllByProductOrderId(Long id);
}
