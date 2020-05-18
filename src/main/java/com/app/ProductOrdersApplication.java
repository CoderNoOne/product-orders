package com.app;

import com.app.domain.entity.Role;
import com.app.domain.entity.User;
import com.app.domain.repository.RoleRepository;
import com.app.domain.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.crypto.SecretKey;
import java.util.List;

@SpringBootApplication
public class ProductOrdersApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ProductOrdersApplication.class, args);
//
//        RoleRepository roleRepository = ctx.getBean("roleRepositoryImpl", RoleRepository.class);
//
//        UserRepository userRepository = ctx.getBean("userRepositoryImpl", UserRepository.class);
//
//
//        System.out.println(userRepository.findByUsername("Michael").get().getRole().getName());
//
//        roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_PRODUCT")
//                .build());
//
//        roleRepository.save(Role.builder()
//                .name("ROLE_USER_MANAGER")
//                .build());
//
//        roleRepository.save(Role.builder()
//                .name("ROLE_USER_CUSTOMER")
//                .build());
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    @Bean
    public List<String> allowedRoles(@Value("${allowedRoles}") List<String> allowedRoles) {
        return allowedRoles;
    }

}
