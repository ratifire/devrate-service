package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested achievement is not found in the system.
 */
public class AchievementNotFoundException extends ResourceNotFoundException {
  public AchievementNotFoundException(String message) {
    super(message);
  }
}
