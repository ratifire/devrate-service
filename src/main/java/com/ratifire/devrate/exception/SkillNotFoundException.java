package com.ratifire.devrate.exception;

/**
 * Exception thrown when a skill not found.
 */
public class SkillNotFoundException extends ResourceNotFoundException {

  public SkillNotFoundException(String message) {
    super(message);
  }
}
