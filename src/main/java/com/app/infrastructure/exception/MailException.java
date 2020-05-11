package com.app.infrastructure.exception;

public class MailException extends RuntimeException {
    public MailException(String message) {
        super(message);
    }
}
