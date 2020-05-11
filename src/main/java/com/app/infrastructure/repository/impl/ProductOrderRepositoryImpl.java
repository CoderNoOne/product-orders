package com.app.infrastructure.repository.impl;

import com.app.domain.entity.ProductOrder;
import com.app.domain.repository.ProductOrderRepository;
import com.app.domain.enums.ProductOrderStatus;
import com.app.infrastructure.repository.jpa.JpaProductOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductOrderRepositoryImpl implements ProductOrderRepository {

    private final JpaProductOrderRepository jpaProductOrderRepository;

    @Override
    public List<ProductOrder> findAll() {
        return jpaProductOrderRepository.findAll();
    }

    @Override
    public Optional<ProductOrder> findOne(Long id) {
        return jpaProductOrderRepository.findById(id);
    }

    @Override
    public ProductOrder save(ProductOrder productOrder) {
        return jpaProductOrderRepository.save(productOrder);
    }

    @Override
    public List<ProductOrder> findAllByUsername(String username) {
        return jpaProductOrderRepository.findAllByCustomerUsername(username);
    }

    @Override
    public void delete(ProductOrder productOrder) {
        jpaProductOrderRepository.delete(productOrder);
    }

    @Override
    public List<ProductOrder> findByUsernameAndProducerName(String username, String producerName) {
        return jpaProductOrderRepository.findAllByUsernameAndProducerName(username, producerName);
    }

    @Override
    public Optional<ProductOrder> findByIdAndCustomerUsername(Long id, String username) {
        return jpaProductOrderRepository.findByIdAndCustomerUsername(id, username);
    }

    @Override
    public List<ProductOrder> saveAll(List<ProductOrder> productOrders) {
        return jpaProductOrderRepository.saveAll(productOrders);
    }

    @Override
    public List<ProductOrder> findAllByUsernameAndStatus(String username, ProductOrderStatus productOrderStatus) {
        return jpaProductOrderRepository.findAllByCustomerUsernameAndStatus(username, productOrderStatus);
    }

    @Override
    public List<ProductOrder> findAllByIdIn(List<Long> productOrdersIds) {
        return jpaProductOrderRepository.findAllByIdIn(productOrdersIds);
    }

    @Override
    public boolean hasProductBeenOrdered(Long id) {
        return jpaProductOrderRepository.hasProductBeenOrdered(id);
    }
}
