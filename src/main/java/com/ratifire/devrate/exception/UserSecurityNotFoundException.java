package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user security is not found.
 */
public class UserSecurityNotFoundException extends ResourceNotFoundException {
  public UserSecurityNotFoundException(String message) {
    super(message);
  }
}
