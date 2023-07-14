package de.hsel.tetris.usermanagement.exception;

import java.util.Map;

public class UserValidationException extends Exception {
    private final Map<String, String> errors;

    public UserValidationException(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
