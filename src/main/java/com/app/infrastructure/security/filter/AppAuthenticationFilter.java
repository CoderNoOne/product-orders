package com.app.infrastructure.security.filter;

import com.app.infrastructure.exception.AppAuthenticationFilterException;
import com.app.infrastructure.security.dto.AuthenticationDto;
import com.app.infrastructure.security.dto.TokensDto;
import com.app.infrastructure.security.tokens.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

// reaguje automatycznie na zadanie /login POST
public class AppAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;


    public AppAuthenticationFilter(AuthenticationManager authenticationManager, TokenManager tokenManager) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;

        // mozesz zmienic zadanie na ktore reaguje ten filter
//        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/my-login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        try {
            AuthenticationDto authenticationDto = new ObjectMapper().readValue(request.getInputStream(), AuthenticationDto.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationDto.getUsername(),
                    authenticationDto.getPassword(),
                    Collections.emptyList()
            ));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new AppAuthenticationFilterException(e.getMessage());
        }
    }

    // kiedy logowanie sie powiedzie wtedy zostanie wywolana metoda ponizej
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        // metoda posiada czwarty argument - authResult
        // przechowuje on informacje na temat zalogowanego usera
        // na jego podstawie wygenerujemy token

        TokensDto tokensDto = tokenManager.generateTokens(authResult);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.CREATED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(tokensDto));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
