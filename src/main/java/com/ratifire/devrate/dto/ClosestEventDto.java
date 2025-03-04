package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.EventType;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the closest calendar events.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClosestEventDto {
  private long id;
  private EventType type;
  private ZonedDateTime startTime;
  private String hostName;
  private String hostSurname;
  private int masteryLevel;
  private String specializationName;
  private String roomUrl;
}
