package com.app.domain.repository;

import com.app.domain.entity.ReservedProduct;
import com.app.domain.generic.CrudRepository;

import java.util.List;

public interface ReservedProductRepository extends CrudRepository<ReservedProduct, Long> {
    List<ReservedProduct> saveAll(List<ReservedProduct> reservedProducts);

    void deleteByProductOrderId(Long id);

    List<ReservedProduct> findAllByProductOrderId(Long id);
}
