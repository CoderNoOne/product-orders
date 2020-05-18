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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;
    private final TokenManager tokenManager;

    @PostMapping("/sign-up-customer")
    public ResponseEntity<ResponseData<Long>> signUpCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {

        var body = ResponseData
                .<Long>builder()
                .data(securityService.registerCustomer(registerCustomerDto))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PostMapping("/sign-up-manager")
    public ResponseEntity<ResponseData<Long>> signUpManager(@RequestBody RegisterManagerDto registerManagerDto) {

        var body = ResponseData
                .<Long>builder()
                .data(securityService.registerManager(registerManagerDto))
                .build();

        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-tokens")
    public ResponseEntity<ResponseData<TokensDto>> refreshTokensUp(@RequestBody RefreshTokenDto refreshTokenDto) {

        var body = ResponseData
                .<TokensDto>builder()
                .data(tokenManager.generateTokens(refreshTokenDto))
                .build();

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
