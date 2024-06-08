package com.ratifire.devrate.exception;

/**
 * Exception thrown when a specialization not found.
 */
public class SpecializationNotFoundException extends ResourceNotFoundException {

  public SpecializationNotFoundException(String message) {
    super(message);
  }
}