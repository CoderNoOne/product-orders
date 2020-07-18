package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = {"producer_id", "name"}))
public class Product extends BaseEntity {

    private String name;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producer;

    @ManyToOne
    @JoinColumn(name = "guarantee_id")
    private Guarantee guarantee;

    public ProductDto toDto() {
        return ProductDto.builder()
                .id(getId())
                .name(name)
                .category(category.getName())
                .price(price)
                .producer(producer.toDto())
                .build();
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public Producer getProducer() {
        return producer;
    }


    public Guarantee getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(Guarantee guarantee) {
        this.guarantee = guarantee;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}

