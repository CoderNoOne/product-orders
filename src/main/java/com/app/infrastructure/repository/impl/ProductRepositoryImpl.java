package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Product;
import com.app.domain.repository.ProductRepository;
import com.app.infrastructure.repository.jpa.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll();
    }

    @Override
    public Optional<Product> findOne(Long id) {
        return jpaProductRepository.findById(id);
    }

    @Override
    public Product save(Product product) {

        return jpaProductRepository.save(product);
    }

    @Override
    public void delete(Product product) {
        jpaProductRepository.delete(product);
    }

    @Override
    public Optional<Product> findByNameAndProducerName(String name, String producerName) {
        return jpaProductRepository.findByNameAndProducerName(name, producerName);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaProductRepository.existsById(id);
    }


}
