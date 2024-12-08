package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "User with id %d not found.";

  public UserNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}
