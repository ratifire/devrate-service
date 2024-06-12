package com.ratifire.devrate.util.zoom.exception;

/**
 * Custom exception class for handling authentication errors related to Zoom.
 */
public class ZoomAuthException extends Exception {
  public ZoomAuthException(String message, Throwable cause) {
    super(message, cause);
  }
}
