package de.hsel.tetris.exception;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException() {
        super("The requested resource does not exist.");
    }
}
