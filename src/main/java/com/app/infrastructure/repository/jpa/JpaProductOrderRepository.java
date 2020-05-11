package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductOrder;
import com.app.domain.enums.ProductOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    List<ProductOrder> findAllByCustomerUsername(String username);

    @Query("select pr from ProductOrder pr where pr.customer.username = :username and pr.product.producer.name = :producerName")
    List<ProductOrder> findAllByUsernameAndProducerName(String username, String producerName);

    Optional<ProductOrder> findByIdAndCustomerUsername(Long id, String username);

    List<ProductOrder> findAllByCustomerUsernameAndStatus(String username, ProductOrderStatus productOrderStatus);

    List<ProductOrder> findAllByIdIn(List<Long> productOrdersIds);

    @Query("select case when count (pr) > 0 then true else false end from ProductOrder pr where pr.product.id = :id")
    boolean hasProductBeenOrdered(Long id);
}
