package com.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDto {

    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;
    private String role;
    private Integer age;
}
