package com.ratifire.devrate.exception;

/**
 * Exception thrown when child object are not found.
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Constructs a new ResourceNotFoundException with the specified detail message.
   *
   * @param message the detail message, explaining why the object was not found.
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }

}
