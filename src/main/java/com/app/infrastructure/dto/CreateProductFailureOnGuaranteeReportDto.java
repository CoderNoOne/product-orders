package com.app.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductFailureOnGuaranteeReportDto {

    private Long complaintId;
    private String selectedServiceType;

    @JsonIgnore
    private String managerUsername;

    public CreateProductFailureOnGuaranteeReportDto managerUsername(String customerUsername) {
        this.managerUsername = customerUsername;
        return this;
    }

}
