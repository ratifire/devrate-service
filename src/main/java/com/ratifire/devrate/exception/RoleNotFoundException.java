package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested role is not found in the system.
 * This exception indicates that the specified role could not be found in the database.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleNotFoundException extends RuntimeException {

    /**
     * Constructs a new RoleNotFoundException with the specified error message.
     *
     * @param name Name of the role that was not found.
     */
    public RoleNotFoundException(String name) {
        super("Role " + name + " is not found!");
    }
}
