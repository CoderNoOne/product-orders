package com.app;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class ProductOrdersApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ProductOrdersApplication.class, args);

//        JpaProductFailureOnGuaranteeRepository jpaProductFailureOnGuaranteeRepository = ctx.getBean("jpaProductFailureOnGuaranteeRepository", JpaProductFailureOnGuaranteeRepository.class);
//
//        jpaProductFailureOnGuaranteeRepository.save(
//                ProductFailureOnGuaranteeReport.builder()
//                        .completionDate(LocalDate.now().plusDays(10))
//                        .status(RepairOrderStatus.PROPOSED)
//                        .damageType(DamageType.PHYSICAL)
//                        .selectedServiceType(GuaranteeComponent.EXCHANGE)
//                        .productOrder()
//                        .build()
//        );

//        RoleRepository roleRepository = ctx.getBean("roleRepositoryImpl", RoleRepository.class);
//
//        UserRepository userRepository = ctx.getBean("userRepositoryImpl", UserRepository.class);
//////
//////
//        var roleAdminProduct = roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_PRODUCT")
//                .build());
//
//        var roleAdminManager = roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_MANAGER")
//                .build());
//
//        var roleUserManager = roleRepository.save(Role.builder()
//                .name("ROLE_USER_MANAGER")
//                .build());
//
//        var roleUserCustomer = roleRepository.save(Role.builder()
//                .name("ROLE_USER_CUSTOMER")
//                .build());
//
//        var roleAdminShop = roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_SHOP")
//                .build());
//
//        var roleAdminActuator = roleRepository.save(Role.builder()
//                .name("ROLE_ADMIN_ACTUATOR")
//                .build());
//
//
////        userRepository.save(User.builder()
////                .role(roleRepository.findByName("ROLE_USER_MANAGER").get())
////                .email("asd")
////                .password("{noop}manager")
////                .username("manager")
////                .build());
//
//        userRepository.save(User.builder()
//                .role(roleAdminActuator)
//                .email("asd")
//                .password("{noop}adminActuator")
//                .username("adminActuator")
//                .build());
//
//        userRepository.save(User.builder()
//                .role(roleAdminShop)
//                .email("as")
//                .password("{noop}adminShop")
//                .username("adminShop")
//                .build());
//
//        userRepository.save(User.builder()
//                .role(roleAdminProduct)
//                .email("asddd")
//                .password("{noop}adminProduct")
//                .username("adminProduct")
//                .build());
//
//        userRepository.save(User.builder()
//                .role(roleAdminManager)
//                .email("asd")
//                .password("{noop}adminManager")
//                .username("adminManager")
//                .build());

    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
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
