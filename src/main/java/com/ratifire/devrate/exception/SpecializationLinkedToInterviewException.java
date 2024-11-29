package com.ratifire.devrate.exception;

/**
 * Exception thrown when specialization has linked scheduled interview.
 */
public class SpecializationLinkedToInterviewException extends RuntimeException {

  public SpecializationLinkedToInterviewException(Long specializationId, Long interviewId) {
    super("Cannot delete specialization with ID " + specializationId
        + " because it has linked interview ID" + interviewId);
  }
}