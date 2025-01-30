package com.ratifire.devrate.util.mirotalk;

/**
 * Custom exception class for handling errors related to MiroTalk API operations.
 */
public class MiroTalkException extends RuntimeException {
  public MiroTalkException(String msg) {
    super(msg);
  }

  public MiroTalkException(String msg, Throwable ex) {
    super(msg, ex);
  }
}