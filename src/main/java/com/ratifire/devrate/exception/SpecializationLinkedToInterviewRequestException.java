package com.ratifire.devrate.exception;

/**
 * Exception thrown when specialization has linked scheduled interview requests.
 */
public class SpecializationLinkedToInterviewRequestException extends RuntimeException {

  public SpecializationLinkedToInterviewRequestException(Long specializationId,
      Long interviewRequestId) {
    super("Cannot delete specialization with ID " + specializationId
        + " because it has linked interview request ID" + interviewRequestId);
  }
}