package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.StockDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import com.app.infrastructure.jackson.ArrayToMapDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "stocks", uniqueConstraints = @UniqueConstraint(columnNames = {"shop_id", "address_id"}))
public class Stock extends BaseEntity {

    @OneToOne()
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ElementCollection
    @CollectionTable(name = "products_quantity")
    @MapKeyJoinColumn(name = "product_id", referencedColumnName = "id")
    @Column(name = "quantity")
    @JsonDeserialize(using = ArrayToMapDeserializer.class,
            keyAs = Product.class, contentAs = Integer.class)
    private Map<Product, Integer> productsQuantity;


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Map<Product, Integer> getProductsQuantity() {
        return productsQuantity;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public StockDto toDto() {

        return StockDto.builder()
                .id(getId())
                .address(address != null ? address.getAddress() : null)
                .productsQuantity(productsQuantity != null ? productsQuantity.entrySet()
                        .stream()
                        .map(e -> ProductQuantityDto.builder()
                                .productInfo(ProductInfo.builder()
                                        .name(e.getKey().getName())
                                        .producerName(e.getKey().getProducer().getName())
                                        .build())
                                .quantity(e.getValue())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public Shop getShop() {
        return shop;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stock{");
        sb.append("address=").append(address);
        sb.append(", shop=").append(shop);
        sb.append(", productsQuantity=").append(productsQuantity);
        sb.append(", id=").append(super.getId());
        sb.append('}');
        return sb.toString();
    }
}
