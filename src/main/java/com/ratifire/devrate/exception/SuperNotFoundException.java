package com.ratifire.devrate.exception;

/**
 * Exception thrown when child object are not found.
 */
public class SuperNotFoundException extends RuntimeException {

  /**
   * Constructs a new SuperNotFoundException with the specified detail message.
   *
   * @param message the detail message, explaining why the object was not found
   */
  public SuperNotFoundException(String message) {
    super(message);
  }

}
