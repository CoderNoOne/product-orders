package com.app.infrastructure.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import io.swagger.models.Scheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

//    v2/api-docs
//    /swagger-ui.html

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2);
//                .select()
//                .paths(paths())
//                .build();
    }
//
//    private Predicate<String> paths() {
//
//        return Predicates.or(
//                regex("/login.*"),
//                regex("/shops/*"),
//                regex("/products/*")
//        );
//    }
}
