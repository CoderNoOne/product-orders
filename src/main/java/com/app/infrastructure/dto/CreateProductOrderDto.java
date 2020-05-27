package com.app.infrastructure.dto;

import com.app.domain.entity.*;
import com.app.domain.enums.ProductOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductOrderDto {

    private String customerUsername;
    private String deliveryAddress;
    private LocalDate paymentDeadline;
    private Long productId;
    private Long shopId;
    private Map<String, Integer> productStockQuantity;
    private BigDecimal discount;
    private Long acceptedProductOrderProposalId;

    public ProductOrder toEntity() {

        return ProductOrder.builder()
                .deliveryAddress(Objects.nonNull(deliveryAddress) ? Address.builder().address(deliveryAddress).build() : null)
                .customer(Objects.nonNull(customerUsername) ? Customer.builder()
                        .username(customerUsername)
                        .build() : null)
                .quantity(Objects.nonNull(productStockQuantity) ? productStockQuantity.values().stream().mapToInt(i -> i).sum() : null)
                .status(ProductOrderStatus.IN_PROGRESS)
                .paymentDeadline(paymentDeadline)
                .discount(discount)
                .product(Objects.nonNull(productId) ? Product.builder().id(productId).build() : null)
                .shop(Objects.nonNull(shopId) ? Shop.builder().id(shopId).build() : null)
                .build();
    }

}
