package com.ratifire.devrate.exception;

/**
 * Exception thrown when an interview summary is not found.
 */
public class InterviewHistoryNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "InterviewHistory with id %d not found.";

  public InterviewHistoryNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }

}
