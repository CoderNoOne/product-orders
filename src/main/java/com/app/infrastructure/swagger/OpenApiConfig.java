package com.app.infrastructure.swagger;

import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.security.dto.AuthenticationDto;
import com.app.infrastructure.security.dto.TokensDto;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.OpenAPIBuilder;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;
import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(
                        new Components()
                                .securitySchemes(Map.of(
                                        "JwtAuthToken",
                                        new SecurityScheme()
                                                .bearerFormat("jwt")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer"),
                                        "CookieAuth",
                                        new SecurityScheme()
                                                .name("remember-me")
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                ))
                ).security(List.of(new SecurityRequirement().addList("JwtAuthToken").addList("CookieAuth")))
                .info(new Info()
                        .title("Product-order rest API")
                        .version("1.0")
                        .contact(new Contact()
                                .email("firelight.code@gmail.com")
                                .name("CoderNoOne")
                                .url("http://www.github.com/CoderNoOne")
                        )
                );
    }

    @Bean
    public GroupedOpenApi loginApi() {
        return GroupedOpenApi.builder()
                .setGroup("login")
                .pathsToMatch("/login/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .path("/login", new PathItem()
                                .post(new Operation()
                                        .addParametersItem(new Parameter().in("path").required(false).name("remember-me"))
                                        .requestBody(new RequestBody()
                                                .required(true)
                                                .content(new Content().addMediaType("application/json", new MediaType()
                                                        .schema(new Schema<AuthenticationDto>().type("object")
                                                                .addProperties(
                                                                        "username", new Schema<String>().type("string"))
                                                                .addProperties(
                                                                        "password", new Schema<String>().type("string"))
                                                        ))))
                                        .responses(new ApiResponses()
                                                .addApiResponse("201",
                                                        new ApiResponse().content(new Content().addMediaType("application/json", new MediaType()
                                                                .schema(new Schema<TokensDto>().type("object")
                                                                        .addProperties(
                                                                                "accessToken", new Schema<String>().type("string")
                                                                        )
                                                                        .addProperties("refreshToken", new Schema<String>().type("string"))
                                                                )
                                                        )).addHeaderObject("Set-Cookie", new Header().schema(new Schema<String>().type("string"))))
                                                .addApiResponse("404", new ApiResponse().content(new Content().addMediaType("application/json", new MediaType()
                                                        .schema(new Schema<ResponseData<String>>().type("object")
                                                                .addProperties(
                                                                        "data", new Schema<String>().type("string"))
                                                                .addProperties("error", new Schema<String>().type("string"))
                                                        ))
                                                ))

                                        )
                                        .tags(List.of("login"))
                                        .security(Collections.emptyList()))))
                .build();
    }

    @Bean
    public GroupedOpenApi managerProductOrderProposalApi() {
        return GroupedOpenApi.builder()
                .setGroup("manager-product-order-proposals")
                .pathsToMatch("/manager/product-order-proposals/**")
                .build();
    }

    @Bean
    public GroupedOpenApi registerVerificationTokenApi() {
        return GroupedOpenApi.builder()
                .setGroup("registerVerificationToken")
                .pathsToMatch("/registerVerificationTokens/**")
                .build();
    }

    @Bean
    public GroupedOpenApi customerProductOrderProposalApi() {
        return GroupedOpenApi.builder()
                .setGroup("customer-product-order-proposals")
                .pathsToMatch("/customer/product-order-proposals/**")
                .build();
    }


    @Bean
    public GroupedOpenApi productFailuresOnGuaranteeApi() {
        return GroupedOpenApi.builder()
                .setGroup("productFailuresOnGuarantee")
                .pathsToMatch("/product-failures-on-guarantee/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productFailuresWithGuaranteeExpiredApi() {
        return GroupedOpenApi.builder()
                .setGroup("productFailuresWithGuaranteeExpired")
                .pathsToMatch("/product-failures-with-guarantee-expired/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productFailuresApi() {
        return GroupedOpenApi.builder()
                .setGroup("product-failures-report")
                .pathsToMatch("/product-failure-reports/**")
                .build();
    }

    @Bean
    public GroupedOpenApi securityApi() {
        return GroupedOpenApi.builder()
                .setGroup("security")
                .pathsToMatch("/security/**")
                .addOpenApiCustomiser(openApi -> openApi.security(Collections.emptyList()))
//                .pathsToExclude("/security/activate")
//                .addOpenApiCustomiser(openApi -> openApi.paths(new Paths().addPathItem("/security/sign-up-customer", new PathItem().post(new Operation().security(Collections.emptyList())))))
//                .addOpenApiCustomiser(openApi -> openApi.path("/security/activate", new PathItem().put(new Operation()))
//                        .security(List.of(new SecurityRequirement().addList("JwtAuthToken").addList("CookieAuth"))))
                .build();
    }

    @Bean
    public GroupedOpenApi stockApi() {
        return GroupedOpenApi.builder()
                .setGroup("stock")
                .pathsToMatch("/stocks/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tradeApi() {
        return GroupedOpenApi.builder()
                .setGroup("trade")
                .pathsToMatch("/trades/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .setGroup("product")
                .pathsToMatch("/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productOrdersApi() {
        return GroupedOpenApi.builder()
                .setGroup("product-order")
                .pathsToMatch("/productOrders/**")
                .build();
    }

    @Bean
    public GroupedOpenApi meetingApi() {
        return GroupedOpenApi.builder()
                .setGroup("meeting")
                .pathsToMatch("/meetings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi repairOrderApi() {
        return GroupedOpenApi.builder()
                .setGroup("repairOrder")
                .pathsToMatch("/repairOrders/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminShopPropertiesApi() {

        return GroupedOpenApi.builder()
                .setGroup("admin-shop-properties")
                .pathsToMatch("/adminShopProperties/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productOrderProposalApi() {
        return GroupedOpenApi.builder()
                .setGroup("product-order-proposals")
                .pathsToMatch("/**/productOrderProposals/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminManagersApi() {
        return GroupedOpenApi.builder()
                .setGroup("admin-managers")
                .pathsToMatch("/admin/managers/**")
                .build();
    }

    @Bean
    public GroupedOpenApi complaintApi() {
        return GroupedOpenApi.builder()
                .setGroup("complaints")
                .pathsToMatch("/complaints/**")
                .build();
    }

    @Bean
    public GroupedOpenApi producerApi() {

        return GroupedOpenApi.builder()
                .setGroup("producer")
                .pathsToMatch("/producers/**")
                .build();
    }

    @Bean
    public GroupedOpenApi shopApi() {

        return GroupedOpenApi.builder()
                .setGroup("shop")
                .pathsToMatch("/shops/**")
                .build();
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
                .setGroup("actuator")
                .pathsToMatch("/actuator/**")
                .addOpenApiCustomiser(openApi -> openApi
                        .path("/actuator/shutdown", new PathItem()
                                .post(new Operation().tags(List.of("actuator"))
                                        .responses(new ApiResponses()
                                                ._default(new ApiResponse()
                                                        .content(new Content()
                                                                .addMediaType("application/json",
                                                                        new MediaType().schema(new Schema<String>()
                                                                                .type("object")
                                                                                .addProperties(
                                                                                        "message", new Schema<String>()
                                                                                                .type("string"))))))))))
                .build();
    }
}
