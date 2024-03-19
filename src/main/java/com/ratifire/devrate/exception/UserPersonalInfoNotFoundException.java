package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user personal info is not found.
 */
public class UserPersonalInfoNotFoundException extends RuntimeException {

  public UserPersonalInfoNotFoundException(String message) {
    super(message);
  }
}
