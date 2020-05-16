package com.app.infrastructure.security.remember_me;

import com.app.domain.repository.UserRepository;
import com.app.infrastructure.security.tokens.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class RememberMeServiceImpl implements RememberMeServices {

    private final TokenManager tokenManager;
    private final UserRepository userRepository;

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        System.out.println("autoLogin");

        AtomicReference<UsernamePasswordAuthenticationToken> authWrapper = new AtomicReference<>();

        if (Objects.nonNull(cookies)) {
            Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("remember-me"))
                    .findFirst()
                    .ifPresent(cookie -> {
                        String accessToken = cookie.getValue();
                        Long userId = tokenManager.getId(accessToken);
                        UsernamePasswordAuthenticationToken auth = userRepository
                                .findOne(userId)
                                .map(userFromDb -> new UsernamePasswordAuthenticationToken(
                                        userFromDb.getUsername(),
                                        null,
                                        List.of(new SimpleGrantedAuthority(userFromDb.getRole().getName()))
                                )).orElseThrow(() -> new SecurityException("no user with id: " + userId));
                        authWrapper.set(auth);
                    });
        }


        return authWrapper.get();
    }

    @Override
    public void loginFail(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        System.out.println("FAIL");
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        System.out.println("LOGIN SUCCCESS");
    }

}
