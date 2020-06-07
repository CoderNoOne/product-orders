package com.app.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateComplaintDto {

    private Long productOrderId;
    private String damageType;

    @JsonIgnore
    private String customerUsername;
}
