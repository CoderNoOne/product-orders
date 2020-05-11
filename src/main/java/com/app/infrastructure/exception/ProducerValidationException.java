package com.app.infrastructure.exception;

public class ProducerValidationException extends RuntimeException{
    public ProducerValidationException(String message) {
        super(message);
    }
}
