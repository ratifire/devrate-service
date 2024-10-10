package com.ratifire.devrate.exception;

/**
 * Exception thrown when invalid input is provided during a user search operation.
 */
public class UserSearchInvalidInputException extends RuntimeException {
  public UserSearchInvalidInputException(String message) {
    super(message);
  }
}