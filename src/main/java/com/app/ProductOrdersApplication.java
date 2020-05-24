package com.app;

import com.app.infrastructure.repository.jpa.JpaStockRepository;
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

        var stockRepository = ctx.getBean("jpaStockRepository", JpaStockRepository.class);
//
        System.out.println(stockRepository.doAllStocksBelongToTheSameShop(1L, List.of(1L, 2L)));

//        var allByIdIn = stockRepository.findAllById(List.of(1L, 2L));
//
//        allByIdIn.forEach(stock -> System.out.println(stock.getProductsQuantity()));



//        var jpaShopRepository = ctx.getBean("jpaShopRepository", JpaShopRepository.class);
//
//        System.out.println(jpaShopRepository.findProductQuantityGroupByName(1L));
//
//        RoleRepository roleRepository = ctx.getBean("roleRepositoryImpl", RoleRepository.class);
//
//        UserRepository userRepository = ctx.getBean("userRepositoryImpl", UserRepository.class);
//
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
//
//        roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_SHOP")
//                .build());
//
//        roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_ACTUATOR")
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
