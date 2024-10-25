package com.ratifire.devrate.exception;


/**
 * Exception thrown when a user reaches the feedback frequency limit.
 */
public class FeedbackSubmissionLimitException extends RuntimeException {

  private static final String MESSAGE = "User with id %d reached feedback frequency limit.";

  public FeedbackSubmissionLimitException(long id) {
    super(String.format(MESSAGE, id));
  }
}

