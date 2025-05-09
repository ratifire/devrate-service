package com.ratifire.devrate.dto.projection;

import java.time.ZonedDateTime;

/**
 * Projection interface for retrieving interview request time slots.
 */
public interface InterviewRequestTimeSlotProjection {

  Long getId();

  ZonedDateTime getDateTime();
}
