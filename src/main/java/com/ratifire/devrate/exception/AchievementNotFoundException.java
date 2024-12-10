package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested achievement is not found in the system.
 */
public class AchievementNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Achievement with id %d not found.";

  public AchievementNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }

}