package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user personal info is not found.
 */
public class UserInfoNotFoundException extends RuntimeException {

  public UserInfoNotFoundException(String message) {
    super(message);
  }
}
