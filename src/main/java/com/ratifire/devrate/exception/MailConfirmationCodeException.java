package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested confirmation code is not found in the system.
 */
public class MailConfirmationCodeException extends MailException {
  public MailConfirmationCodeException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}
