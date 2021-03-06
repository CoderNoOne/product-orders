package com.app.domain.entity;

import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.DamageType;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ComplaintDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "complaints")
public class Complaint extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private ProductOrder productOrder;

    private final LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    private DamageType damageType;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    public ComplaintDto toDto() {

        return ComplaintDto.builder()
                .id(getId())
                .damageType(damageType)
                .issueDate(issueDate)
                .productOrderDto(Objects.nonNull(productOrder) ? productOrder.toDto() : null)
                .complaintStatus(status.name())
                .build();
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public ProductOrder getProductOrder() {
        return productOrder;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public Complaint(ProductOrder productOrder, DamageType damageType, ComplaintStatus status) {
        super();
        this.productOrder = productOrder;
        this.damageType = damageType;
        this.status = status;
        this.issueDate = LocalDate.now();
    }

    public Complaint() {
        super();
        this.issueDate = LocalDate.now();
    }


    public DamageType getDamageType() {
        return damageType;
    }
}
