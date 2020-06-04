package com.app.infrastructure.controller;

import com.app.application.service.RegisterVerificationTokenService;
import com.app.infrastructure.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registerVerificationTokens")
public class RegisterVerificationTokenController {

    private final RegisterVerificationTokenService registerVerificationTokenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Long> requestNewRegisterVerificationToken() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseData.<Long>builder()
                .data(registerVerificationTokenService.save(username))
                .build();

    }
}
