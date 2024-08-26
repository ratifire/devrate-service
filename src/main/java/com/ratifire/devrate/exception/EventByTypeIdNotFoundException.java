package com.ratifire.devrate.exception;

/**
 * Exception thrown when an event by type id is not found.
 */
public class EventByTypeIdNotFoundException extends ResourceNotFoundException {

  private static final String MESSAGE_ID_NOT_FOUND = "Event with event type id %d not found.";

  public EventByTypeIdNotFoundException(long id) {
    super(String.format(MESSAGE_ID_NOT_FOUND, id));
  }
}
