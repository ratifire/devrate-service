package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user security is not found.
 */
public class UserSecurityNotFoundException extends SuperNotFoundException {
  public UserSecurityNotFoundException(String message) {
    super(message);
  }
}
