package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user EmploymentRecord info not found.
 */
public class EmploymentRecordNotFoundException extends ResourceNotFoundException {

  public EmploymentRecordNotFoundException(String message) {
    super(message);
  }
}
