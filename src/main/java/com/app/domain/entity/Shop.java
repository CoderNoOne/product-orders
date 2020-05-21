package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ShopDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "shops")
@XmlRootElement
public class Shop extends BaseEntity {

    @Column(unique = true)
    private String name;
    private BigDecimal budget;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Stock> stocks;

    public Address getAddress() {
        return address;
    }

    public Set<Stock> getStocks() {
        return stocks;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ShopDto toDto(){

        return ShopDto.builder()
                .id(getId())
                .name(name)
                .budget(budget)
                .address(address != null ? address.getAddress() : null)
                .stocks(stocks != null ? stocks.stream().map(Stock::toDto).collect(Collectors.toList()) : Collections.emptyList())
                .build();

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "name='" + name + '\'' +
                '}';
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public String getName() {
        return name;
    }
}
