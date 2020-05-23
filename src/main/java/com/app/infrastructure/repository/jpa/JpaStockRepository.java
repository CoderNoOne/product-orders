package com.app.infrastructure.repository.jpa;

import ch.qos.logback.core.boolex.EvaluationException;
import com.app.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface JpaStockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByIdAndShopId(Long id, Long shopId);

    Optional<Stock> findByAddressAddressAndShopId(String address, Long shopId);

    @Query("select (count(distinct st.shop.id) = 1) from Stock st where st.id = :stockFrom or st.id = :stockTo")
    boolean doStocksBelongToTheSameShop(@Param("stockFrom") Long stockFrom, @Param("stockTo") Long stockTo);

    Optional<Stock> findById(Long id);

    @Query("select value (pr) from Stock st join st.productsQuantity pr where st.id = :stockId and exists (select pr from st.productsQuantity pr where key(pr).name = :name and key(pr).producer.name = :producerName)")
    Integer findProductQuantityInStock(String name, String producerName, Long stockId);

    @Query("select case when (count (s.productsQuantity.size) > 0) then true else false end from Stock s join s.productsQuantity pr where exists (select pr from s.productsQuantity pr where key(pr).id = :productId)")
    boolean doProductExistsInAnyStock(Long productId);

    @Query(value = "select s from Stock s join fetch s.productsQuantity where s.id in :ids")
    List<Stock> findAllByIdIn(Collection<Long> ids);

    @Query(value = "select distinct st.shop.id from Stock st where st.id in (:stockIds)")
    List<Long> getShopsForStocks(List<Long> stockIds);

    default boolean doAllStocksBelongToTheSameShop(Long shopId, List<Long> stockIds) {
        var shopsForStocks = getShopsForStocks(stockIds);
        return shopsForStocks.size() == 1 && Objects.equals(shopsForStocks.get(0), shopId);
    }
}
