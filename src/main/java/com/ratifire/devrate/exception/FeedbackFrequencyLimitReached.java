package com.ratifire.devrate.exception;


/**
 * Exception thrown when a user reaches the feedback frequency limit.
 */
public class FeedbackFrequencyLimitReached extends RuntimeException {

  private static final String MESSAGE = "User with id %d reached feedback frequency limit.";

  public FeedbackFrequencyLimitReached(long id) {
    super(String.format(MESSAGE, id));
  }
}

