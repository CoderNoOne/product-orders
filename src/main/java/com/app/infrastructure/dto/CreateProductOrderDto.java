package com.app.infrastructure.dto;

import com.app.domain.entity.Address;
import com.app.domain.entity.Producer;
import com.app.domain.entity.ProductOrder;
import com.app.domain.enums.ProductOrderStatus;
import com.app.domain.entity.Product;
import com.app.infrastructure.dto.createShop.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateProductOrderDto {

    private ProductInfo productInfo;
    private Integer quantity;
    private Long stockId;
    private String deliveryAddress;

    public ProductOrder toEntity() {
        return ProductOrder.builder()
                .product(Product.builder()
                        .producer(Producer.builder()
                                .name(Objects.isNull(productInfo) ? null : productInfo.getProducerName())
                                .build())
                        .name(Objects.isNull(productInfo) ? null : productInfo.getName())
                        .build())
                .status(ProductOrderStatus.IN_PROGRESS)
                .deliveryAddress(Address.builder().address(deliveryAddress).build())
                .quantity(quantity)
                .build();
    }
}
