package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user work experience info already exists.
 */
public class WorkExperienceAlreadyExistException extends RuntimeException {

  public WorkExperienceAlreadyExistException(String massage) {
    super(massage);
  }

}
