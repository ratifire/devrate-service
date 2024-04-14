package com.ratifire.devrate.exception;

/**
 * Exception thrown when attempting to confirm registration with an expired email confirmation code.
 */
public class MailConfirmationCodeExpiredException extends SuperMailException {

  public MailConfirmationCodeExpiredException(String message) {
    super(message);
  }
}
