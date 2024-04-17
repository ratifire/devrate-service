package com.ratifire.devrate.exception;

/**
 * Exception thrown when a email is not found.
 */
public class EmailNotFoundException extends RuntimeException {
  public EmailNotFoundException(String message) {
    super(message);
  }
}
