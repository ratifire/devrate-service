package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview is not found.
 */
public class InterviewNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview with id %d not found.";

  public InterviewNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}