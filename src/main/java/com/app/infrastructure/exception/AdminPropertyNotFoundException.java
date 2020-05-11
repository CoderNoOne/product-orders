package com.app.infrastructure.exception;

public class AdminPropertyNotFoundException extends RuntimeException {
    public AdminPropertyNotFoundException(String message) {
        super(message);
    }
}
