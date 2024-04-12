package com.ratifire.devrate.exception;

/**
 * Exception thrown when attempting to create a new language proficiency of the user that already
 * exists in the system. This exception indicates that a language proficiency with the same name
 * already exists
 */
public class LanguageProficiencyAlreadyExistException extends RuntimeException {

  public LanguageProficiencyAlreadyExistException(String message) {
    super(message);
  }

}
