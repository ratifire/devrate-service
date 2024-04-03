package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user personal info already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
