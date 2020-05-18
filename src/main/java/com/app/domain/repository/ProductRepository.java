package com.app.domain.repository;

import com.app.domain.entity.Product;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository <Product, Long> {

    void delete(Product product);

    Optional<Product> findByNameAndProducerName(String name, String producerName);

    boolean existsById(Long id);

    List<Product> findAllByCategory(String category);
}
