package com.ratifire.devrate.exception;


/**
 * Exception thrown when a requested interview feedback detail is not found in the system.
 */
public class InterviewFeedbackDetailNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Interview feedback detail "
      + "with id %d not found.";

  public InterviewFeedbackDetailNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}