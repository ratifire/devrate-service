package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.EventType;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the calendar event.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  private long id;
  private EventType type;
  private String roomLink;
  private ZonedDateTime startTime;

}