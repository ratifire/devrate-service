package com.ratifire.devrate.exception;

/**
 * Exception to indicate that a contact was not found. This exception should be thrown when
 * attempting to retrieve a contact that does not exist in the system.
 */
public class ContactNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_NOT_FOUND_FORMAT = "Contact with id %d not found";

  public ContactNotFoundException(long id) {
    super(String.format(MESSAGE_NOT_FOUND_FORMAT, id));
  }
}
