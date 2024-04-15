package com.ratifire.devrate.exception;

/**
 * Exception thrown when child object are already exists.
 */
public class SuperAlreadyExistException extends RuntimeException {

  /**
   * Constructs a new SuperAlreadyExistException with the specified detail message.
   *
   * @param message the detail message.
   */
  public SuperAlreadyExistException(String message) {
    super(message);
  }
}
