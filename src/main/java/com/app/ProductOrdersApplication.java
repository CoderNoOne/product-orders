package com.app;

import com.app.application.service.EmailService;
import com.app.application.service.MailTemplates;
import com.app.domain.embbedable.ProposalRemark;
import com.app.infrastructure.dto.CustomerDto;
import com.app.infrastructure.dto.MeetingDto;
import com.app.infrastructure.dto.NoticeDto;
import com.app.infrastructure.dto.ProductOrderProposalDto;
import com.app.infrastructure.dto.createShop.ProductInfo;
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

        emailService.sendAsHtml(null, "firelight.code@gmail.com", MailTemplates.notifyManagerAboutAddedNotice(

                "manager usern name",
                NoticeDto.builder()
                        .meetingDto(MeetingDto.builder()
                                .customerDto(CustomerDto.builder()
                                        .username("customer username")
                                        .build())
                                .orderProposalId(1L)
                                .meetingDate(LocalDate.of(2002,10,25))
                                .build())
                        .tittle("Tytuł tej notatki")
                        .content("jakism tam gowno content asdadadlkjashdajkdhakjdhaskjdhasjdkhasjdashdjkashdkjsahdaskj")
                        .build(),
                ProductOrderProposalDto.builder()
                        .address("ul dzbanan 123")
                        .discount(BigDecimal.ZERO)
                        .productInfo(ProductInfo.builder()
                                .name("gowno")
                                .producerName("szombierki")
                                .build())
                        .quantity(10)
                        .remarks(List.of(ProposalRemark.builder()
                                .tittle("asdasd")
                                .content("asDasdasdahsdsgtdhfjsgdvfjhgdsfjsadgfasdjkfgsadkyfjgsdajkfgasdjfmgsxadjhfgsadjfghdxzjfgSZHDfdhsgfhjsfghsdkmjfgasdjfahdsfh")
                                .build(),
                                ProposalRemark.builder()
                                        .tittle("Asdasdasdhaksdhgasdkjfshde")
                                        .content("asdhakjhdfiuytwfsdkhfhauresfghsdkjfysdlfkjshdiurwsdfgkawerytdsukjfhreaiugfhdsaugayrewgfkjdsaufirehesiutreligt;uergiuesaruiesdkjfhsdkjfhsdjkfhsdkjfhsdjkfhsdkjfhywiuerywieuryweiuryweiurywiueryweyiurwe" +
                                                "werwer" +
                                                "ewrw" +
                                                "erwer" +
                                                "werwerwerwerwerewr")
                                        .build()


                        ))

                        .build()

        ), "titleess");
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
