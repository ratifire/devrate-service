package com.ratifire.devrate.exception;

/**
 * Exception thrown when attempting to create a new user with an email address that already exists
 * in the system. This exception indicates that a user with the same email address already exists
 * and prevents duplicate user creation.
 */
public class UserSecurityAlreadyExistException extends ResourceAlreadyExistException {
  public UserSecurityAlreadyExistException(String message) {
    super(message);
  }
}
