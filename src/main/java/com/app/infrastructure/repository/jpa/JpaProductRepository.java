package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProductRepository extends JpaRepository <Product, Long> {

    void delete(Product Product);

    Optional<Product> findByNameAndProducerName(String name, String producerName);

    boolean existsById(Long id);
}
