package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user personal info is not found.
 */
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
