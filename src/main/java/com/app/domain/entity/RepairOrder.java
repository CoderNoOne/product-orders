package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "repair_orders")
public class RepairOrder extends BaseEntity {

    private LocalDate completionDate;
    private BigDecimal repairCosts;

    @OneToOne
    @JoinColumn(name = "product_order_id", referencedColumnName = "id")
    private ProductOrder productOrder;
}
