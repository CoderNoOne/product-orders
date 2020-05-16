package com.app.infrastructure.swagger;

import com.app.infrastructure.security.dto.AuthenticationDto;
import com.app.infrastructure.security.dto.TokensDto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customAPI() {
        return new OpenAPI().components(
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
                                        .in(SecurityScheme.In.COOKIE)
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
                )
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

                                        )
                                        .tags(List.of("login"))
                                        .security(Collections.emptyList())
                        ))
                .path("/security/sign-up-customer", new PathItem()
                        .post(new Operation().security(Collections.emptyList())))
                .path("/security/sign-up-manager", new PathItem()
                        .post(new Operation().security(Collections.emptyList())))
                .path("/security/refresh-tokens", new PathItem()
                        .post(new Operation().security(Collections.emptyList())));
    }

}
