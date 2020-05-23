package com.app.domain.repository;

import com.app.domain.entity.Stock;
import com.app.domain.generic.CrudRepository;

import java.util.Collection;
import java.util.List;
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

    List<Stock> findAllByIdIn(Collection<Long> ids);

    boolean doAllStocksBelongToTheSameShop(Long shopId, List<Long> stockIds);

}
