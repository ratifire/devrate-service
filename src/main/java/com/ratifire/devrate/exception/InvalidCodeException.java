package com.ratifire.devrate.exception;

/**
 * Exception thrown when an invalid token is provided.
 * This exception indicates that the specified token is invalid.
 */
public class InvalidCodeException extends RuntimeException {

    public InvalidCodeException(String message) {
        super(message);
    }
}
