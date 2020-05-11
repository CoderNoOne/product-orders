package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOrderDetails {

    private String productName;
    private String producerName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDate orderDate;
    private LocalDate paymentDeadline;
    private String deliveryAddress;
}
