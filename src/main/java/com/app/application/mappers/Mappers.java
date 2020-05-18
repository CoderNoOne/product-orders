package com.app.application.mappers;

import com.app.domain.entity.Customer;
import com.app.domain.entity.Manager;
import com.app.domain.entity.User;
import com.app.infrastructure.dto.CustomerEmailAndUsername;

import java.util.Objects;

// @Component
// @RequiredArgsConstructor
public interface Mappers {



//    static User fromDtoToEntity(RegisterUserDto registerUserDto) {
//        return registerUserDto == null ? null : User
//                .builder()
//                .username(registerUserDto.getUsername())
//                .email(registerUserDto.getEmail())
//                .password(registerUserDto.getPassword())
////                .roles(new HashSet<>())
//                .build();
//    }

    static Customer fromUserToCustomer(User user, Integer age) {
        return Objects.isNull(user) ?
                null :
                Customer.builder()
                        .role(user.getRole())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .age(age)
                        .build();
    }

    static Manager fromUserToManager(User user) {
        return Objects.isNull(user) ?
                null :
                Manager.builder()
                        .role(user.getRole())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .build();
    }

    static CustomerEmailAndUsername fromCustomerToCustomerEmailAndUsername(Customer customer){
        return Objects.nonNull(customer) ? CustomerEmailAndUsername.builder()
                .email(customer.getEmail())
                .username(customer.getUsername())
                .build() : null;
    }
}
