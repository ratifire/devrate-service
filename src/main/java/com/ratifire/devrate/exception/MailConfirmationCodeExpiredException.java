package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to confirm registration with an expired email confirmation code.
 */
public class MailConfirmationCodeExpiredException extends MailException {
  public MailConfirmationCodeExpiredException(String message) {
    super(message, HttpStatus.GONE);
  }
}
