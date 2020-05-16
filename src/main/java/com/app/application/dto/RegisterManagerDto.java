package com.app.application.dto;

import com.app.domain.entity.Manager;
import com.app.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegisterManagerDto {

    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;

    public Manager toEntity() {
        return Manager.builder()
                .role(Role.builder().name("ROLE_USER_MANAGER").build())
                .username(username)
                .email(email)
                .password(password)
                .build();
    }
}
