package com.app.domain.entity;


import com.app.domain.converters.jpa.PeriodToStringConverter;
import com.app.domain.enums.GuaranteeComponent;
import com.app.domain.generic.BaseEntity;

import com.app.domain.other.Period;
import com.app.infrastructure.dto.GuaranteeDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;


import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "guarantees")
public class Guarantee extends BaseEntity {

    private String name;
    private Integer percent;

    @Convert(converter = PeriodToStringConverter.class)
    private Period guaranteeTime;

    @Convert(converter = PeriodToStringConverter.class)
    private Period guaranteeProcessingTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guarantee_components")
    @Enumerated(EnumType.STRING)
    private List<GuaranteeComponent> guaranteeComponents;

    @ManyToOne
    @JoinColumn(name = "producer_id")
    private Producer producer;

    @OneToMany(mappedBy = "guarantee")
    @EqualsAndHashCode.Exclude
    private Set<Product> product;

//    public String getName() {
//        return name;
//    }

    public GuaranteeDto toDto() {
        return GuaranteeDto.builder()
                .guaranteeProcessingTime(guaranteeProcessingTime)
                .guaranteeTime(guaranteeTime)
                .percent(percent)
                .build();
    }

    @Override
    public String toString() {
        return "Guarantee{" +
                "name='" + name + '\'' +
                ", percent=" + percent +
                ", guaranteeTime=" + guaranteeTime +
                ", guaranteeProcessingTime=" + guaranteeProcessingTime +
                ", guaranteeComponents=" + guaranteeComponents +
                ", producer=" + producer +
                '}';
    }
}

