package com.app;

import com.app.application.service.EmailService;
import com.app.application.service.MailTemplates;
import com.app.domain.entity.Product;
import com.app.domain.enums.DamageType;
import com.app.domain.enums.GuaranteeComponent;
import com.app.infrastructure.dto.*;
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
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class ProductOrdersApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ProductOrdersApplication.class, args);

        var emailService = ctx.getBean("emailService", EmailService.class);


        var s = MailTemplates.generateHtmlInfoAboutCompletionDate(ProductFailureOnGuaranteeReportDto.builder()
                .completionDate(LocalDate.of(2020, 10, 20))
                .selectedService(GuaranteeComponent.REPAIR)
                .damageType(DamageType.PHYSICAL)
                .productOrderDto(ProductOrderDto.builder()
                        .customerDto(CustomerDto.builder()
                                .username("Dzban")
                                .email("firelight.code@gmail.com")
                                .build())
                        .productDto(ProductDto.builder()
                                .name("Notebook")
                                .build())
                        .build())
                .build());

        emailService.sendAsHtml(null, "firelight.code@gmail.com", s, "asdasd");
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
