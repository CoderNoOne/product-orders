package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface JpaShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByName(String name);

    void deleteById(Long id);

    @Query(value = "select s from Shop s JOIN FETCH s.stocks st where exists (select pr from st.productsQuantity pr where key(pr).id = :productId and value(pr) > 0) order by value(st.productsQuantity) DESC")
    Set<Shop> findAllWhereProductExists(@Param("productId") Long productId);
}
