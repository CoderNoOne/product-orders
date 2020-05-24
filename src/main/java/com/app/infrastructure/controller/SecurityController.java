package com.app.infrastructure.controller;

import com.app.application.dto.RegisterCustomerDto;
import com.app.application.dto.RegisterManagerDto;
import com.app.application.service.SecurityService;
import com.app.infrastructure.dto.RefreshTokenDto;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.security.dto.TokensDto;
import com.app.infrastructure.security.tokens.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;
    private final TokenManager tokenManager;

    @PostMapping("/sign-up-customer")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> signUpCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {

        return ResponseData
                .<Long>builder()
                .data(securityService.registerCustomer(registerCustomerDto))
                .build();
    }

    @PostMapping("/sign-up-manager")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> signUpManager(@RequestBody RegisterManagerDto registerManagerDto) {

        return ResponseData
                .<Long>builder()
                .data(securityService.registerManager(registerManagerDto))
                .build();
    }

    @PostMapping("/refresh-tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<TokensDto> refreshTokensUp(@RequestBody RefreshTokenDto refreshTokenDto) {

        return ResponseData
                .<TokensDto>builder()
                .data(tokenManager.generateTokens(refreshTokenDto))
                .build();
    }

}
