package com.app.infrastructure.exception_handlers;

import com.app.application.exception.RegisterUserException;
import com.app.infrastructure.dto.ResponseData;
import com.app.infrastructure.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseData<String>> handleNotFoundException(NotFoundException exception) {

        log.error(exception.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(exception.getMessage())
                        .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegisterUserException.class)
    public ResponseEntity<ResponseData<String>> handleRegisterUserException(RegisterUserException registerUserException) {

        log.error(registerUserException.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(registerUserException.getMessage())
                        .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShopValidationException.class)
    public ResponseEntity<ResponseData<String>> handleRegisterUserException(ShopValidationException productValidationException) {

        log.error(productValidationException.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(productValidationException.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullReferenceException.class)
    public ResponseEntity<ResponseData<String>> handleNullReferenceException(NullReferenceException nullReferenceException) {

        log.error(nullReferenceException.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(nullReferenceException.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseData<String>> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {

        log.error(constraintViolationException.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(constraintViolationException.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseData<String>> handleConstraintViolationException(ValidationException validationException) {

        log.error(validationException.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(validationException.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(StaleObjectStateException.class)
    public ResponseEntity<ResponseData<String>> handleOptimisticLockingException(StaleObjectStateException staleObjectStateException) {

        log.error(staleObjectStateException.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(staleObjectStateException.getMessage())
                        .build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AdminPropertyNotFoundException.class)
    public ResponseEntity<ResponseData<String>> handleAdminPropertyNotFoundException(AdminPropertyNotFoundException exception) {

        log.error(exception.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(exception.getMessage())
                        .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseData<String>> handleIllegalStateException(IllegalStateException exception) {

        log.error(exception.getMessage());

        return new ResponseEntity<>(
                ResponseData.<String>builder()
                        .error(exception.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }
}
