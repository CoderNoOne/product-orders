package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ReservedProduct;
import com.app.domain.repository.ReservedProductRepository;
import com.app.infrastructure.repository.jpa.JpaReservedProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservedProductRepositoryImpl implements ReservedProductRepository {

    private final JpaReservedProductRepository jpaReservedProductRepository;

    @Override
    public List<ReservedProduct> findAll() {
        return jpaReservedProductRepository.findAll();
    }

    @Override
    public Optional<ReservedProduct> findOne(Long id) {
        return jpaReservedProductRepository.findById(id);
    }

    @Override
    public ReservedProduct save(ReservedProduct reservedProduct) {
        return jpaReservedProductRepository.save(reservedProduct);
    }

    @Override
    public List<ReservedProduct> saveAll(List<ReservedProduct> reservedProducts) {
        return jpaReservedProductRepository.saveAll(reservedProducts);
    }

    @Override
    public void deleteByProductOrderId(Long id) {
        jpaReservedProductRepository.deleteByProductOrderId(id);
    }

    @Override
    public List<ReservedProduct> findAllByProductOrderId(Long id) {
        return jpaReservedProductRepository.findAllByProductOrderId(id);
    }
}
