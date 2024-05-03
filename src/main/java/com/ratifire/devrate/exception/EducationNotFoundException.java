package com.ratifire.devrate.exception;

/**
 * Exception thrown when a requested education is not found in the system.
 */
public class EducationNotFoundException extends ResourceNotFoundException {
  public EducationNotFoundException(String message) {
    super(message);
  }
}
