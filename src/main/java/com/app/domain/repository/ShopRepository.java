package com.app.domain.repository;

import com.app.domain.entity.Shop;
import com.app.domain.generic.CrudRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ShopRepository extends CrudRepository<Shop, Long> {
    Optional<Shop> findByName(String name);
    void deleteById(Long id);

    Set<Shop> findAllShopsWithProductInStore(Long id);

    Integer findProductCountInAllStocks(Long shopId, Long id);

    Map<Shop, Integer> findProductQuantityGroupByShop(Long productId);
}
