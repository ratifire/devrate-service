package com.ratifire.devrate.util.zoom.exception;

/**
 * Custom exception class for handling errors related to Zoom API operations.
 */
public class ZoomApiException extends RuntimeException {
  public ZoomApiException(String msg) {
    super(msg);
  }

  public ZoomApiException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
