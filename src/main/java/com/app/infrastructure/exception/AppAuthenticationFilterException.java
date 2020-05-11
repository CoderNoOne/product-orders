package com.app.infrastructure.exception;

public class AppAuthenticationFilterException extends RuntimeException {
    public AppAuthenticationFilterException(String message) {
        super(message);
    }
}
