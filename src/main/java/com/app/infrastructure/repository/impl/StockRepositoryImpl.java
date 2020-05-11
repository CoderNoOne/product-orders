package com.app.infrastructure.repository.impl;

import com.app.domain.entity.Product;
import com.app.domain.entity.Stock;
import com.app.domain.repository.StockRepository;
import com.app.infrastructure.repository.jpa.JpaStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final JpaStockRepository jpaStockRepository;

    @Override
    public List<Stock> findAll() {
        return jpaStockRepository.findAll();
    }

    @Override
    public Optional<Stock> findOne(Long id) {
        return jpaStockRepository.findById(id);
    }

    @Override
    public Stock save(Stock stock) {
        return jpaStockRepository.save(stock);
    }

    @Override
    public void deleteAll(Set<Stock> stocks) {
        jpaStockRepository.deleteAll(stocks);
    }

    @Override
    public Optional<Stock> findByIdAndShopId(Long stockId, Long shopId) {
        return jpaStockRepository.findByIdAndShopId(stockId, shopId);
    }

    @Override
    public Optional<Stock> findByAddressAndShopId(String address, Long shopId) {
        return jpaStockRepository.findByAddressAddressAndShopId(address, shopId);
    }

    @Override
    public void delete(Stock stock) {
        jpaStockRepository.delete(stock);
    }

    @Override
    public boolean doStocksBelongToTheSameShop(Long stockFrom, Long stockTo) {
        return jpaStockRepository.doStocksBelongToTheSameShop(stockFrom, stockTo);
    }

    @Override
    public Integer findProductQuantityInStock(String name, String producerName, Long stockId) {
        return jpaStockRepository.findProductQuantityInStock(name, producerName, stockId);
    }

    @Override
    public boolean doProductExistsInAnyStock(Long productId) {
        return jpaStockRepository.doProductExistsInAnyStock(productId);
    }
}
