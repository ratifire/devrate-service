package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview request is not found.
 */
public class InterviewRequestNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview request with id %d not found.";

  public InterviewRequestNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}