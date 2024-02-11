package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a new user with an email address that already exists in the system.
 * This exception indicates that a user with the same email address
 * already exists and prevents duplicate user creation.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistException with the specified error message.
     *
     * @param message The detail message of the exception.
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
