package com.ratifire.devrate.exception;

/**
 * Exception to indicate that a language was not found. This exception should be thrown when
 * attempting to retrieve a language that does not exist in the system.
 */
public class LanguageNotFoundException extends RuntimeException {

  private static final String MESSAGE_NOT_FOUND_FORMAT = "Language with id %d not found";

  public LanguageNotFoundException(long id) {
    super(String.format(MESSAGE_NOT_FOUND_FORMAT, id));
  }

}