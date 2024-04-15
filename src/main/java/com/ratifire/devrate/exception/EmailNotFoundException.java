package com.ratifire.devrate.exception;

/**
 * Exception thrown when a email is not found.
 */
public class EmailNotFoundException extends RuntimeException {

  /**
   * Constructs a new EmailNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public EmailNotFoundException(String message) {
    super(message);
  }
}
