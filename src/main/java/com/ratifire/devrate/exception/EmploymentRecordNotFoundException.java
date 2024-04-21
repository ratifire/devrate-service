package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user EmploymentRecord info already exists.
 */
public class EmploymentRecordNotFoundException extends ResourceNotFoundException {

  public EmploymentRecordNotFoundException(String message) {
    super(message);
  }
}
