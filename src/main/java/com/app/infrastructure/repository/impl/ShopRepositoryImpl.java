package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Shop;
import com.app.domain.repository.ShopRepository;
import com.app.infrastructure.repository.jpa.JpaShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ShopRepositoryImpl implements ShopRepository {

    private final JpaShopRepository jpaShopRepository;

    @Override
    public List<Shop> findAll() {
        return jpaShopRepository.findAll();
    }

    @Override
    public Optional<Shop> findOne(Long id) {
        return jpaShopRepository.findById(id);
    }

    @Override
    public Shop save(Shop shop) {
        return jpaShopRepository.save(shop);
    }

    @Override
    public Optional<Shop> findByName(String name) {
        return jpaShopRepository.findByName(name);
    }

    @Override
    public void deleteById(Long id) {
        jpaShopRepository.deleteById(id);
    }

    @Override
    public Set<Shop> findAllShopsWithProductInStore(Long id) {
        return jpaShopRepository.findAllWhereProductExists(id);
    }
}
