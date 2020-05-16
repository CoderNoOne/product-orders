package com.app.infrastructure.dto;

import com.app.domain.entity.Manager;
import com.app.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateManagerDto {

    private String username;
    private String email;

    public Manager toEntity() {
        return Manager.builder()
                .email(email)
                .username(username)
                .role(Role.builder().name("USER_MANAGER").build())
                .build();
    }
}
