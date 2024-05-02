package com.ratifire.devrate.exception;

/**
 * Exception thrown when a Specialisation not found.
 */
public class SpecialisationNotFoundException extends ResourceNotFoundException {

  public SpecialisationNotFoundException(String message) {
    super(message);
  }
}
