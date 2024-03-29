package com.ratifire.devrate.exception;

/**
 * Exception thrown when attempting to confirm registration with an expired email confirmation code.
 */
public class EmailConfirmationCodeExpiredException extends RuntimeException {

  public EmailConfirmationCodeExpiredException(String message) {
    super(message);
  }
}
