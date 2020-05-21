package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.StockDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
import com.app.infrastructure.dto.createShop.ProductQuantityDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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
    private Map<Product, Integer> productsQuantity;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setProductsQuantity(Map<Product, Integer> productsQuantity) {
        this.productsQuantity = productsQuantity;
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


}
