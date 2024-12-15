package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user EmploymentRecord info already exists.
 */
public class EmploymentRecordNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Employment record with id %d not found.";

  public EmploymentRecordNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}
