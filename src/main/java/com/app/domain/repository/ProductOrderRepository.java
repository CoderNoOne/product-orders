package com.app.domain.repository;

import com.app.domain.entity.ProductOrder;
import com.app.domain.enums.ProductOrderStatus;
import com.app.domain.generic.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOrderRepository extends CrudRepository<ProductOrder, Long> {

    List<ProductOrder> findAllByUsername(String username);

    void delete(ProductOrder productOrder);

    List<ProductOrder> findByUsernameAndProducerName(String username, String producerName);

    Optional<ProductOrder> findByIdAndCustomerUsername(Long id, String username);

    List<ProductOrder> saveAll(List<ProductOrder> productOrders);

    List<ProductOrder> findAllByUsernameAndStatus(String username, ProductOrderStatus productOrderStatus);

    List<ProductOrder> findAllByIdIn(List<Long> productOrdersIds);

    boolean hasProductBeenOrdered(Long id);
}
