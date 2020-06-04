package com.app.application.dto;

import com.app.domain.entity.Customer;
import com.app.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCustomerDto {

    private String username;
    private String email;
    private String password;
    private String passwordConfirmation;
    private Integer age;
    private String gender;

    public Customer toEntity(){

        return Customer.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(Role.builder().name("ROLE_USER_CUSTOMER").build())
                .enabled(false)
                .age(age)
                .build();
    }
}
