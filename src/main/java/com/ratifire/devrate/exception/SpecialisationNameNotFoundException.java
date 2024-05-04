package com.ratifire.devrate.exception;

/**
 * Exception thrown when a Specialisation not found.
 */
public class SpecialisationNameNotFoundException extends ResourceNotFoundException {

  public SpecialisationNameNotFoundException(String message) {
    super(message);
  }
}
