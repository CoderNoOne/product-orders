package com.app.infrastructure.exception_handlers;

import com.app.application.exception.RegisterUserException;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler  {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseData<String> handleNotFoundException(/*NotFoundException*/ Exception exception) {

        log.error(exception.getMessage());

        return ResponseData.<String>builder()
                .error(exception.getMessage())
                .build();
    }

    @ExceptionHandler(RegisterUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleRegisterUserException(RegisterUserException registerUserException) {

        log.error(registerUserException.getMessage());

        return ResponseData.<String>builder()
                .error(registerUserException.getMessage())
                .build();
    }

    @ExceptionHandler(ShopValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleRegisterUserException(ShopValidationException productValidationException) {

        log.error(productValidationException.getMessage());

        return ResponseData.<String>builder()
                .error(productValidationException.getMessage())
                .build();
    }

    @ExceptionHandler(NullReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleNullReferenceException(NullReferenceException nullReferenceException) {

        log.error(nullReferenceException.getMessage());

        return ResponseData.<String>builder()
                .error(nullReferenceException.getMessage())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {

        log.error(constraintViolationException.getMessage());

        return ResponseData.<String>builder()
                .error(constraintViolationException.getMessage())
                .build();
    }


    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleConstraintViolationException(ValidationException validationException) {

        log.error(validationException.getMessage());

        return ResponseData.<String>builder()
                .error(validationException.getMessage())
                .build();
    }


    @ExceptionHandler(StaleObjectStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseData<String> handleOptimisticLockingException(StaleObjectStateException staleObjectStateException) {

        log.error(staleObjectStateException.getMessage());

        return ResponseData.<String>builder()
                .error(staleObjectStateException.getMessage())
                .build();
    }

    @ExceptionHandler(AdminPropertyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseData<String> handleAdminPropertyNotFoundException(AdminPropertyNotFoundException exception) {

        log.error(exception.getMessage());

        return ResponseData.<String>builder()
                .error(exception.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleIllegalStateException(IllegalStateException exception) {

        log.error(exception.getMessage());

        return ResponseData.<String>builder()
                .error(exception.getMessage())
                .build();
    }

    @ExceptionHandler(AppAuthenticationFilterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<String> handleAppAuthenticationFilterException(AppAuthenticationFilterException exception) {

        log.error(exception.getMessage());

        return ResponseData.<String>builder()
                .error(exception.getMessage())
                .build();
    }

}
