package com.ratifire.devrate.exception;

/**
 * MailException represents an exception that occurs during email sending or handling.
 */
public class MailException extends RuntimeException {

  /**
   * Constructs a new MailException with the specified detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage()).
   */
  public MailException(String message) {
    super(message);
  }

}
