package com.ratifire.devrate.exception;

/**
 * Exception thrown when child object are already exists.
 */
public class ResourceAlreadyExistException extends RuntimeException {
  public ResourceAlreadyExistException(String message) {
    super(message);
  }
}
