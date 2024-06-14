package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview summary is not found.
 */
public class InterviewSummaryNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview Summary with id %d not found.";

  public InterviewSummaryNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }

}
