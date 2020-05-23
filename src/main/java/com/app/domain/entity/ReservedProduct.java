package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "reserved_products")
public class ReservedProduct extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_order_id")
    private ProductOrder productOrder;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    public void setProductOrder(ProductOrder productOrder) {
        this.productOrder = productOrder;
    }

    public Stock getStock() {
        return this.stock;
    }

    public Integer getQuantity() {
        return this.quantity;
    }
}
