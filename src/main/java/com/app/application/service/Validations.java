package com.app.application.service;

import java.util.Map;
import java.util.stream.Collectors;

public interface Validations {

    static String createErrorMessage(Map<String, String> errors) {
        return errors
                .entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(", ", "Validations errors: [", "]"));

    }
}
