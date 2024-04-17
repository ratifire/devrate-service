package com.ratifire.devrate.exception;

/**
 * MailException represents an exception that occurs during email sending or handling.
 */
public class MailException extends RuntimeException {

  public MailException(String message) {
    super(message);
  }
}
