package com.app.infrastructure.security.filter;

import com.app.infrastructure.security.tokens.TokenManager;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

// ten filter bedzie wykonywal sie dla kazdego requesta
public class AppAuthorizationFilter extends BasicAuthenticationFilter {

    private final TokenManager tokenManager;

    public AppAuthorizationFilter(AuthenticationManager authenticationManager, TokenManager tokenManager) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(accessToken)) {
            UsernamePasswordAuthenticationToken auth = tokenManager.parseToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
