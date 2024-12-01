package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the password reset process.
 */
public class PasswordResetException extends RuntimeException {

  public PasswordResetException(String message) {
    super(message);
  }
}