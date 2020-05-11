package com.app.infrastructure.security.config;

import com.app.infrastructure.security.dto.AppError;
import com.app.infrastructure.security.filter.AppAuthenticationFilter;
import com.app.infrastructure.security.filter.AppAuthorizationFilter;
import com.app.infrastructure.security.tokens.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class AppWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final TokenManager tokenManager;


    public AppWebSecurityConfig(
            @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
            TokenManager tokenManager) {
        this.userDetailsService = userDetailsService;
        this.tokenManager = tokenManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(
                    HttpServletRequest httpServletRequest,
                    HttpServletResponse response,
                    AuthenticationException e) throws IOException, ServletException {

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(422);
                response.getWriter().write(new ObjectMapper().writeValueAsString(AppError
                        .builder()
                        .error(e.getMessage())
                        .build()));
                response.getWriter().flush();
                response.getWriter().close();
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(
                    HttpServletRequest httpServletRequest,
                    HttpServletResponse response,
                    AccessDeniedException e) throws IOException, ServletException {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(422);
                response.getWriter().write(new ObjectMapper().writeValueAsString(AppError
                        .builder()
                        .error(e.getMessage())
                        .build()));
                response.getWriter().flush();
                response.getWriter().close();
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger-resources/configuration/ui","/swagger-ui.html").permitAll()
                .antMatchers("/security/**").permitAll()
                .antMatchers("/products/**").hasRole("ADMIN_PRODUCT")
                .antMatchers("/shops/**").hasRole("ADMIN_SHOP")
                .antMatchers("/stocks/**").hasRole("USER_MANAGER")
                .antMatchers("/customer/**").hasRole("USER_CUSTOMER")
                .anyRequest().authenticated()

                .and()
                .addFilter(new AppAuthenticationFilter(authenticationManager(), tokenManager))
                .addFilter(new AppAuthorizationFilter(authenticationManager(), tokenManager));
    }

}
