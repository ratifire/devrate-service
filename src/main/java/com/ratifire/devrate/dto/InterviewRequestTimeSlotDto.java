package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.TimeSlotStatus;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for representing interview request time slots.
 */
@Builder
@Getter
public class InterviewRequestTimeSlotDto {
  private long id;
  private ZonedDateTime dateTime;
  private TimeSlotStatus status;
}