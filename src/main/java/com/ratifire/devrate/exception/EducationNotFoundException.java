package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested education is not found in the system. This exception indicates
 * that the specified education could not be found in the database.
 */
public class EducationNotFoundException extends ResourceNotFoundException {
  public EducationNotFoundException(String message) {
    super(message);
  }
}
