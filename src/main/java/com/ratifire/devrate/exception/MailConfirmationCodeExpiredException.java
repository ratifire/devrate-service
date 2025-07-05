package com.ratifire.devrate.exception;

/**
 * Exception thrown when a mail confirmation code has expired.
 */
public class MailConfirmationCodeExpiredException extends RuntimeException {

  public MailConfirmationCodeExpiredException(String message) {
    super(message);
  }
}