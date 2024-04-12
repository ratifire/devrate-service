package com.ratifire.devrate.exception;

/**
 * Exception to indicate that a language proficiency was not found. This exception should be thrown
 * when attempting to retrieve a language proficiency that does not exist in the system.
 */
public class LanguageProficiencyNotFoundException extends RuntimeException {

  private static final String MESSAGE_NOT_FOUND_FORMAT =
      "Language proficiency with id %d not found";

  public LanguageProficiencyNotFoundException(long id) {
    super(String.format(MESSAGE_NOT_FOUND_FORMAT, id));
  }

}