package com.ratifire.devrate.util.mirotalk;

/**
 * Custom exception class for handling errors related to MiroTalk API operations.
 */
public class MeetingServiceException extends RuntimeException {
  public MeetingServiceException(String msg) {
    super(msg);
  }

  public MeetingServiceException(String msg, Throwable ex) {
    super(msg, ex);
  }
}