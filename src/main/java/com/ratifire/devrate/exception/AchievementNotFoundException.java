package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested achievement is not found in the system. This exception
 * indicates that the specified achievement could not be found in the database.
 */
public class AchievementNotFoundException extends ResourceNotFoundException {
  public AchievementNotFoundException(String message) {
    super(message);
  }
}
