package com.ratifire.devrate.exception;

/**
 * Exception indicating that a user's avatar could not be found.
 * This exception is thrown when an operation that requires a user's
 * avatar to exist fails because the avatar is missing, such as during an
 * attempt to update or delete an avatar that does not exist.
 */
public class UserAvatarNotFoundException extends RuntimeException {
  public UserAvatarNotFoundException(String message) {
    super(message);
  }
}
