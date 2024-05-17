package com.ratifire.devrate.exception;

/**
 * Exception thrown when a Niche not found.
 */
public class NicheNotFoundException extends ResourceNotFoundException {

  public NicheNotFoundException(String message) {
    super(message);
  }
}
