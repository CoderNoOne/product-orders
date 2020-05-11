package com.app.application.validators.generic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractValidator<T> implements Validator<T> {

    protected Map<String, String> errors = new ConcurrentHashMap<>();

    @Override
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}


