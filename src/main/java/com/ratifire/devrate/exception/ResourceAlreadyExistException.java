package com.ratifire.devrate.exception;

/**
 * Exception thrown when child object are already exists.
 */
public class ResourceAlreadyExistException extends RuntimeException {

  /**
   * Constructs a new ResourceAlreadyExistException with the specified detail message.
   *
   * @param message the detail message.
   */
  public ResourceAlreadyExistException(String message) {
    super(message);
  }
}
