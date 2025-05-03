package com.ratifire.devrate.exception;

/**
 * Exception thrown when there is an error related to mail confirmation codes.
 */
public class MailConfirmationCodeException extends RuntimeException {

  public MailConfirmationCodeException(String message) {
    super(message);
  }
}