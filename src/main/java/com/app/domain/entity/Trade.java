package com.app.domain.entity;

import com.app.domain.entity.Producer;
import com.app.domain.generic.BaseEntity;
import com.app.infrastructure.dto.TradeDto;
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
@Table(name = "trades")
public class Trade extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "trade")
    private List<Producer> producers;

    public TradeDto toDto() {
        return TradeDto.builder()
                .id(super.getId())
                .name(this.name)
                .build();
    }

    public String getName() {
        return name;
    }

    public List<Producer> getProducers() {
        return producers;
    }
}
