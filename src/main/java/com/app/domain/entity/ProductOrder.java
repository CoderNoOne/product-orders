package com.app.domain.entity;

import com.app.domain.enums.ProductOrderStatus;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ProductOrderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Table(name = "product_orders")
public class ProductOrder extends BaseEntity {

    private Integer quantity;
    private BigDecimal discount;
    private BigDecimal penalty;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private ProductOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;

    private LocalDate orderDate;
    private LocalDate paymentDeadline;

    public void setProduct(Product product) {
        this.product = product;
    }


    public ProductOrderDto toDto() {

        return ProductOrderDto.builder()
                .id(getId())
                .quantity(quantity)
                .orderDate(orderDate)
                .productDto(Objects.nonNull(product) ? product.toDto() : null)
                .status(status)
                .penalty(penalty)
                .discount(discount)
                .deliveryAddress(Objects.nonNull(deliveryAddress) ? deliveryAddress.getAddress() : null)
                .paymentDeadline(paymentDeadline)
                .build();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setPaymentDeadline(LocalDate paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setStatus(ProductOrderStatus status) {
        this.status = status;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductOrder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public ProductOrder product(Product product) {
        this.product = product;
        return this;
    }

    public ProductOrder orderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public ProductOrder quantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductOrder deliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public ProductOrder shop(Shop shop) {
        this.shop = shop;
        return this;
    }
}
