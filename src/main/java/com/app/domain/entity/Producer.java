package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ProducerDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "producers")
public class Producer extends BaseEntity {

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @OneToMany(mappedBy = "producer")
    private Set<Product> products;

    @OneToMany(mappedBy = "producer")
    private Set<Guarantee> guarantees;

    public ProducerDto toDto() {
        return ProducerDto.builder()
                .id(super.getId())
                .name(this.name)
                .tradeName(this.trade != null ? this.trade.getName() : null)
                .guarantees(Objects.nonNull(guarantees) ? guarantees.stream().map(Guarantee::toDto).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public String getName() {
        return name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Set<Guarantee> getGuarantees() {
        return guarantees;
    }

    public void setGuarantees(Set<Guarantee> guarantees) {
        this.guarantees = guarantees;
    }
}
