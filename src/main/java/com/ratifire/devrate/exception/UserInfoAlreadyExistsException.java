package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user personal info already exists.
 */
public class UserInfoAlreadyExistsException extends RuntimeException {
  public UserInfoAlreadyExistsException(String message) {
    super(message);
  }
}
