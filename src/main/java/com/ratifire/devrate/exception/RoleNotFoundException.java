package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested role is not found in the system. This exception indicates that
 * the specified role could not be found in the database.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleNotFoundException extends RuntimeException {

  public RoleNotFoundException(String message) {
    super(message);
  }
}
