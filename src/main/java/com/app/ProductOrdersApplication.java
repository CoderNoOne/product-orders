package com.app;

import com.app.application.service.EmailService;
import com.app.application.service.MailTemplates;
import com.app.infrastructure.dto.OrderDateBoundaryDto;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class ProductOrdersApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(ProductOrdersApplication.class, args);

        var emailService = ctx.getBean("emailService", EmailService.class);

        emailService.sendAsHtml(null,"firelight.code@gmail.com",
                MailTemplates.generateHtmlInfoAboutTotalPrice("usernbame", new BigDecimal("100.20"), OrderDateBoundaryDto.builder()

                        .from(LocalDate.of(2002,10,20))
                        .build()), "ttile");
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
