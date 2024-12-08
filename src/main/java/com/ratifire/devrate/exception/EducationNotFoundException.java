package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested education is not found in the system.
 */
public class EducationNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Education with id %d not found.";

  public EducationNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}
