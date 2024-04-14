package com.ratifire.devrate.exception;

/**
 * Exception indicating that the provided file type is unsupported.
 * This exception is typically thrown in scenarios where a user attempts to upload
 * or process a file of a type that the application does not support, such as trying
 * to upload an executable file when only image files are allowed.
 */
public class UnsupportedFileTypeException extends RuntimeException {
  public UnsupportedFileTypeException(String message) {
    super(message);
  }
}
