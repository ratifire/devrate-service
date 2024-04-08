package com.ratifire.devrate.exception;

/**
 * Exception thrown when a user work experience info already exists.
 */
public class WorkExperienceNotFoundException extends RuntimeException {

  public WorkExperienceNotFoundException(String massage) {
    super(massage);
  }

}
