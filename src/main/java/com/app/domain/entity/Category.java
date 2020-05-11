package com.app.domain.entity;

import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "categories")
public class Category extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public CategoryDto toDto() {

        return CategoryDto.builder()
                .id(super.getId())
                .name(this.name)
                .build();
    }

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return products;
    }
}

