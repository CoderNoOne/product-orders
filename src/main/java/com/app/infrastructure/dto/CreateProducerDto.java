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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateProducerDto {

    private String name;
    private String tradeName;
    private List<GuaranteeDto> guarantees;


    public Producer toEntity() {
        return Producer.builder()
                .name(name)
                .trade(Objects.nonNull(tradeName) ? Trade.builder().name(tradeName).build() : null)
                .products(new HashSet<>())
                .guarantees(guarantees != null ? guarantees.stream().map(GuaranteeDto::toEntity).collect(Collectors.toSet()) : new HashSet<>())
                .build();
    }
}
