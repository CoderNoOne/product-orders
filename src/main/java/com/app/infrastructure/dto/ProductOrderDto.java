package com.app.infrastructure.dto;

import com.app.domain.enums.ProductOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderDto {

    private Long id;
    private ProductDto productDto;
    private BigDecimal penalty;
    private Integer quantity;
    private BigDecimal discount;
    private ProductOrderStatus status;
    private LocalDate paymentDeadline;
    private LocalDate orderDate;
    private String deliveryAddress;

}
