package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user work experience info already exists.
 */
public class EmploymentRecordNotFoundException extends RuntimeException {

  public EmploymentRecordNotFoundException(String message) {
    super(message);
  }

}
