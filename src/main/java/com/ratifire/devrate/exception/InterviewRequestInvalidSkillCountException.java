package com.ratifire.devrate.exception;

/**
 * Thrown when an interview request cannot be created due to an invalid number of hard/soft skills.
 */
public class InterviewRequestInvalidSkillCountException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Interview request with id %d has invalid skill "
      + "count: hard=%d, soft=%d.";

  public InterviewRequestInvalidSkillCountException(long interviewRequestId, long hardSkillCount,
      long softSkillCount) {
    super(String.format(DEFAULT_MESSAGE, interviewRequestId, hardSkillCount, softSkillCount));
  }
}
