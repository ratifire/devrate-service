package com.ratifire.devrate.exception;

/**
 * Exception indicating an issue with the request for email confirmation code.
 * This exception is thrown when there is an error related to the request for email confirmation
 * code.
 */
public class EmailConfirmationCodeRequestException extends RuntimeException {

  public EmailConfirmationCodeRequestException(String message) {
    super(message);
  }
}
