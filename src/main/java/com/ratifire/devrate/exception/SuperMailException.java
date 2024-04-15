package com.ratifire.devrate.exception;

/**
 * SuperMailException represents an exception that occurs during email sending or handling.
 */
public class SuperMailException extends RuntimeException {

  /**
   * Constructs a new SuperMailException with the specified detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage()
   *                method)
   */
  public SuperMailException(String message) {
    super(message);
  }

}
