package com.app.domain.entity;

import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.AdminShopPropertyDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "admin_shop_properties")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString
public class AdminShopProperty extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private AdminShopPropertyName property;

    private BigDecimal value;

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public AdminShopPropertyDto toDto(){
        return AdminShopPropertyDto.builder()
                .name(Objects.nonNull(property) ? property.name() : null)
                .value(value)
                .build();
    }

    public BigDecimal getValue() {
        return value;
    }
}
