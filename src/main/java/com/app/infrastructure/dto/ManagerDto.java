package com.app.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ManagerDto {

    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private List<CustomerDto> customers;
}
