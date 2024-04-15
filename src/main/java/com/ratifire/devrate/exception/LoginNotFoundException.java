package com.ratifire.devrate.exception;

/**
 * Exception thrown when a login is not found.
 */
public class LoginNotFoundException extends ResourceNotFoundException {

  /**
   * Constructs a new LoginNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public LoginNotFoundException(String message) {
    super(message);
  }
}
