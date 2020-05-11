package com.app.infrastructure.controller;

import com.app.application.dto.RegisterUserDto;
import com.app.application.service.SecurityService;
import com.app.infrastructure.dto.RefreshTokenDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.security.dto.TokensDto;
import com.app.infrastructure.security.tokens.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;
    private final TokenManager tokenManager;

    @PostMapping("/sign-up")
    public ResponseData<Long> signUp(@RequestBody RegisterUserDto registerUserDto) {
        return ResponseData
                .<Long>builder()
                .data(securityService.register(registerUserDto))
                .build();
    }

    @PostMapping("/refresh-tokens")
    public ResponseData<TokensDto> refreshTokensUp(@RequestBody RefreshTokenDto refreshTokenDto) {
        return ResponseData
                .<TokensDto>builder()
                .data(tokenManager.generateTokens(refreshTokenDto))
                .build();
    }

}
