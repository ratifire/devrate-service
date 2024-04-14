package com.ratifire.devrate.exception;

/**
 * Exception indicating that a user already has an avatar set.
 * This exception is thrown during avatar creation operations if the
 * target user already possesses an avatar, enforcing the rule that each
 * user may only have one avatar at a time.
 */
public class UserAvatarAlreadyExistException extends RuntimeException {
  public UserAvatarAlreadyExistException(String message) {
    super(message);
  }
}
