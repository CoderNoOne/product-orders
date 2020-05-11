package com.app.domain.entity;

import com.app.domain.enums.ComplaintStatus;
import com.app.domain.enums.DamageType;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ComplaintDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "complaints")
public class Complaint extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private ProductOrder productOrder;

    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    private DamageType damageType;
    private ComplaintStatus status;

    public ComplaintDto toDto() {

        return ComplaintDto.builder()
                .id(getId())
                .damageType(damageType)
                .issueDate(issueDate)
                .productOrderDto(Objects.nonNull(productOrder) ? productOrder.toDto() : null)
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
}
