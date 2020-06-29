package com.app.infrastructure.security.filter;

import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.security.dto.AuthenticationDto;
import com.app.infrastructure.security.dto.TokensDto;
import com.app.infrastructure.security.tokens.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;


@RequiredArgsConstructor
public class AppAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            AuthenticationDto authenticationDto = new ObjectMapper().readValue(request.getInputStream(), AuthenticationDto.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationDto.getUsername(),
                    authenticationDto.getPassword(),
                    Collections.emptyList()
            ));
        } catch (Exception e) {
            logger.error(e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseData.<String>builder().error(e.getMessage()).build()));
            response.getWriter().flush();
            response.getWriter().close();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        TokensDto tokensDto = tokenManager.generateTokens(authResult);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.CREATED.value());

        if (Objects.nonNull(request.getParameter("remember-me"))) {

            Cookie cookie = new Cookie("remember-me", tokensDto.getAccessToken());
            cookie.setMaxAge(200000);
            response.addCookie(cookie);
        }

        response.getWriter().write(new ObjectMapper().writeValueAsString(tokensDto));
        response.getWriter().flush();
        response.getWriter().close();

        chain.doFilter(request, response);
    }
}
