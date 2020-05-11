package com.app.application.exception;

public class RegisterUserException extends RuntimeException {
    public RegisterUserException(String error) {
        super(error);
    }
}
