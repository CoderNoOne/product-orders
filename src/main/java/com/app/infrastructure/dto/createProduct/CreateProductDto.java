package com.app.infrastructure.dto.createProduct;

import com.app.domain.entity.Category;
import com.app.domain.entity.Product;
import com.app.infrastructure.dto.GuaranteeDto;
import com.app.infrastructure.dto.ProducerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDto {

    private String name;
    private BigDecimal price;
    private String categoryName;
    private ProducerDto producer;
    private GuaranteeDto guarantee;

    public Product toEntity() {
        return Product.builder()
                .name(name)
                .price(price)
                .category(categoryName != null ? Category.builder()
                        .name(categoryName)
                        .build() : null)
                .producer(producer != null ? producer.toEntity() : null)
                .guarantee(guarantee.toEntity())
                .build();
    }
}
