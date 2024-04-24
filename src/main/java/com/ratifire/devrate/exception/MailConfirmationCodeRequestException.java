package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception indicating an issue with the request for email confirmation code.
 */
public class MailConfirmationCodeRequestException extends MailException {
  public MailConfirmationCodeRequestException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}
