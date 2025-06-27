package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the password change.
 */
public class PasswordChangeException extends RuntimeException {
  public PasswordChangeException(String message) {
    super(message);
  }
}