package com.app.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SecurityException extends RuntimeException {
    public SecurityException(String message) {
        super(message);
    }
}
