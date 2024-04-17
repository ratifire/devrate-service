package com.ratifire.devrate.exception;

/**
 * Exception thrown when a login is not found.
 */
public class LoginNotFoundException extends ResourceNotFoundException {
  public LoginNotFoundException(String message) {
    super(message);
  }
}
