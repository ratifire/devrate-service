package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested confirmation code is not found in the system.
 * This exception indicates that the specified email confirmation code could not
 * be found in the database.
 */
public class MailConfirmationCodeException extends MailException {
  public MailConfirmationCodeException(String message) {
    super(message);
  }
}
