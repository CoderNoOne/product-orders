package com.app.infrastructure.dto;

import com.app.domain.entity.Producer;
import com.app.domain.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProducerDto {

    private Long id;
    private String name;
    private String tradeName;
    private List<GuaranteeDto> guarantees;


    public Producer toEntity() {
        return Producer.builder()
                .id(id)
                .name(name)
                .trade(Objects.isNull(tradeName) ? Trade.builder().build() : null)
                .products(new HashSet<>())
                .guarantees(guarantees.stream().map(GuaranteeDto::toEntity).collect(Collectors.toSet()))
                .build();
    }
}
