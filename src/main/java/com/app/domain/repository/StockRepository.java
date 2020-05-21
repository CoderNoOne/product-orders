package com.app.domain.repository;

import com.app.domain.entity.Stock;
import com.app.domain.generic.CrudRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface StockRepository extends CrudRepository<Stock, Long> {
    void deleteAll(Set<Stock> stocks);

    Optional<Stock> findByIdAndShopId(Long stockId, Long shopId);

    Optional<Stock> findByAddressAndShopId(String address, Long shopId);

    void delete(Stock stock);

    boolean doStocksBelongToTheSameShop(Long stockFrom, Long stockTo);

    Integer findProductQuantityInStock(String name, String producerName, Long stockId);

    boolean doProductExistsInAnyStock(Long productId);

    Set<Stock> findAllByIdIn(Collection<Long> ids);

    Boolean doAllStocksBelongToTheSameShop(Set<Long> stockIds, Long shopId, Integer stockSize);
}
