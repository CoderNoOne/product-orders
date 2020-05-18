package com.app.infrastructure.security.config;

import com.app.domain.repository.UserRepository;
import com.app.infrastructure.security.dto.AppError;
import com.app.infrastructure.security.filter.AppAuthenticationFilter;
import com.app.infrastructure.security.filter.AppAuthorizationFilter;
import com.app.infrastructure.security.remember_me.RememberMeServiceImpl;
import com.app.infrastructure.security.tokens.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
public class AppWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final TokenManager tokenManager;
    private final UserRepository userRepository;
    private final RememberMeServices rememberMeServiceImpl;


    public AppWebSecurityConfig(
            @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
            TokenManager tokenManager, UserRepository userRepository, RememberMeServices rememberMeServiceImpl) {
        this.userDetailsService = userDetailsService;
        this.tokenManager = tokenManager;
        this.userRepository = userRepository;
        this.rememberMeServiceImpl = rememberMeServiceImpl;
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
                .antMatchers("/security/**").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-resources/configuration/ui", "/swagger-ui.html").permitAll()
                .antMatchers("/swagger-ui/**", "/swagger/**", "/v3/**").permitAll()

                .antMatchers(HttpMethod.GET, "/shops**").hasAnyRole("USER_CUSTOMER", "USER_MANAGER", "ADMIN_SHOP")
                .antMatchers(HttpMethod.GET, "/products**").hasAnyRole("USER_CUSTOMER", "ADMIN_PRODUCT")
                .antMatchers(HttpMethod.GET, "/producers**").hasAnyRole("USER_CUSTOMER", "ADMIN_PRODUCT")
                .antMatchers(HttpMethod.GET, "/trades**").hasAnyRole("USER_CUSTOMER", "ADMIN_PRODUCT")
                .antMatchers(HttpMethod.GET, "/meetings**", "/meetings/**").hasAnyRole("USER_MANAGER", "USER_CUSTOMER")

                .antMatchers("/customer/productOrderProposals/**").hasRole("USER_CUSTOMER")
                .antMatchers("/customer/**").hasRole("USER_CUSTOMER")

                .antMatchers("/stocks/**", "/meetings/**").hasRole("USER_MANAGER")
                .antMatchers("/manager/productOrderProposals/**").hasRole("USER_MANAGER")
                .antMatchers("/shops/**", "/shops**").hasRole("ADMIN_SHOP")
                .antMatchers("/products/**", "/products**").hasRole("ADMIN_PRODUCT")
                .antMatchers("/actuator/**").hasRole("ADMIN_ACTUATOR")
                .antMatchers("/managers/**").hasRole("ADMIN_MANAGER")
                .anyRequest().authenticated()


                .and()
                .addFilter(new AppAuthenticationFilter(authenticationManager(), tokenManager, rememberMeServiceImpl))
                .addFilter(new AppAuthorizationFilter(authenticationManager(), tokenManager, userRepository));
    }
}
