package com.app.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShopValidationException extends RuntimeException {
    public ShopValidationException(String message) {
        super(message);
    }
}
