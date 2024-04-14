package com.ratifire.devrate.exception;

/**
 * Exception indicating that required file data is missing or unreadable.
 * This exception is thrown when an operation on a file fails due to missing data,
 * such as when the file data cannot be read correctly due to I/O errors or corruption.
 */
public class MissingFileDataException extends RuntimeException {
  public MissingFileDataException(String message) {
    super(message);
  }
}
