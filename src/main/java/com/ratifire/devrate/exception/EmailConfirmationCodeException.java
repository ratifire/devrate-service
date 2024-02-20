package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested confirmation code is not found in the system.
 * This exception indicates that the specified email confirmation code could not
 * be found in the database.
 */
public class EmailConfirmationCodeException extends RuntimeException {
  public EmailConfirmationCodeException(String message) {
    super(message);
  }
}
