package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the logout process.
 */
public class LogoutException extends RuntimeException {
  public LogoutException(String message) {
    super(message);
  }
}
