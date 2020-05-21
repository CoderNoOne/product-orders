package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Shop;
import com.app.infrastructure.dto.projection.ShopProductQuantity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface JpaShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByName(String name);

    void deleteById(Long id);

    @Query(value = "select s from Shop s JOIN FETCH s.stocks st where exists (select pr from st.productsQuantity pr where key(pr).id = :productId and value(pr) > 0) order by value(st.productsQuantity) DESC")
    Set<Shop> findAllWhereProductExists(@Param("productId") Long productId);

    @Query(value = "select sum(value(pr)) from Shop s  JOIN  s.stocks st join st.productsQuantity pr where s.id = :shopId and exists (select pr from st.productsQuantity pr where key(pr).id = :productId)")
    Integer findProductCountInAllStocks(Long shopId, Long productId);


    @Query(value = "select new com.app.infrastructure.dto.projection.ShopProductQuantity(shop.name, value(pr)) from Shop as shop JOIN  shop.stocks st join st.productsQuantity pr where exists (select pr from st.productsQuantity pr where key(pr).id = :productId and value(pr) > 0)")
    List<ShopProductQuantity> findProductQuantityGroupByName(Long productId);
}
