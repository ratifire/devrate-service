package com.ratifire.devrate.exception;

/**
 * Exception indicating an issue with the request for email confirmation code.
 * This exception is thrown when there is an error related to the request for email confirmation
 * code.
 */
public class MailConfirmationCodeRequestException extends SuperMailException {

  public MailConfirmationCodeRequestException(String message) {
    super(message);
  }
}
