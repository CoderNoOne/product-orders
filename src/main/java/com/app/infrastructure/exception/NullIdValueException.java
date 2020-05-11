package com.app.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class NullIdValueException extends RuntimeException{
    public NullIdValueException(String message) {
        super(message);
    }
}
