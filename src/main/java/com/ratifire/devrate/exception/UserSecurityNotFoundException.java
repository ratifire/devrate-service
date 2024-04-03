package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user is not found.
 */
public class UserSecurityNotFoundException extends RuntimeException {
  public UserSecurityNotFoundException(String message) {
    super(message);
  }
}
