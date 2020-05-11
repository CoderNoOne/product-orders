package com.app.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NullReferenceException extends RuntimeException {
    public NullReferenceException(String message) {
        super(message);
    }
}
