package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested role is not found in the system. This exception indicates that
 * the specified role could not be found in the database.
 */
public class RoleNotFoundException extends ResourceNotFoundException {
  public RoleNotFoundException(String message) {
    super(message);
  }
}
