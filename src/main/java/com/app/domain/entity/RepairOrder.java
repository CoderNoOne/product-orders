package com.app.domain.entity;

import com.app.domain.enums.RepairOrderStatus;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.RepairOrderDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@Table(name = "repair_orders")
public class RepairOrder extends BaseEntity {

    private LocalDate completionDate;
    private BigDecimal repairCosts;


    @Enumerated(EnumType.STRING)
    private RepairOrderStatus status;

    @OneToOne
    @JoinColumn(name = "product_order_id", referencedColumnName = "id")
    private ProductOrder productOrder;

    public RepairOrderDto toDto() {

        return RepairOrderDto.builder()
                .id(getId())
                .completionDate(completionDate)
                .repairCosts(repairCosts)
                .productOrder(Objects.nonNull(productOrder) ? productOrder.toDto() : null)
                .build();
    }
}
