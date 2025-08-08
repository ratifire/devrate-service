package com.ratifire.devrate.security.exception;

/**
 * Exception thrown when an error occurs during the email change.
 */
public class EmailChangeException extends RuntimeException {

  public EmailChangeException(String message) {
    super(message);
  }
}