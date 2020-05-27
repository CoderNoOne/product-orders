package com.app.infrastructure.security.filter;

import com.app.domain.repository.UserRepository;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.security.tokens.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

// ten filter bedzie wykonywal sie dla kazdego requesta
public class AppAuthorizationFilter extends BasicAuthenticationFilter {

    private final TokenManager tokenManager;
    private final UserRepository userRepository;

    public AppAuthorizationFilter(AuthenticationManager authenticationManager, TokenManager tokenManager, UserRepository userRepository) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
        this.userRepository = userRepository;

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
        } else {
            Cookie[] cookies = request.getCookies();

            if (Objects.nonNull(cookies)) {
                Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals("remember-me"))
                        .findFirst()
                        .ifPresent(cookie -> {
                            String accessToken2 = cookie.getValue();
                            Long userId = tokenManager.getId(accessToken2);
                            UsernamePasswordAuthenticationToken auth = userRepository
                                    .findOne(userId)
                                    .map(userFromDb -> new UsernamePasswordAuthenticationToken(
                                            userFromDb.getUsername(),
                                            null,
                                            List.of(new SimpleGrantedAuthority(userFromDb.getRole().getName()))
                                    )).orElseThrow(() -> new SecurityException("no user with id: " + userId));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            }
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            PrintWriter writer = response.getWriter();
            response.setStatus(400);
            response.setContentType("application/json");
            writer.write(new ObjectMapper().writeValueAsString(ResponseData.<String>builder().error(e.getMessage()).build()));
            writer.close();
            writer.flush();
        }

    }
}


