package com.ratifire.devrate.exception;

/**
 * Exception thrown when an object can't serialise/deserialize Json.
 */
public class JsonConverterException extends RuntimeException {

  public JsonConverterException(String message, Throwable cause) {
    super(message, cause);
  }
}
