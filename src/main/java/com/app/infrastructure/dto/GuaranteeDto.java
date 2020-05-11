package com.app.infrastructure.dto;

import com.app.domain.entity.Guarantee;
import com.app.domain.enums.GuaranteeComponent;
import com.app.domain.other.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GuaranteeDto {

    private String name;
    private Integer percent;
    private Period guaranteeTime;
    private Period guaranteeProcessingTime;
    private List<GuaranteeComponent> guaranteeComponents;

    public Guarantee toEntity(){
        return Guarantee.builder()
                .name(name)
                .guaranteeProcessingTime(guaranteeProcessingTime)
                .percent(percent)
                .guaranteeTime(guaranteeTime)
                .guaranteeComponents(guaranteeComponents)
                .build();
    }
}
