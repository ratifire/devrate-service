package com.ratifire.devrate.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an invalid token is provided.
 */
public class InvalidCodeException extends MailException {
  public InvalidCodeException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}
