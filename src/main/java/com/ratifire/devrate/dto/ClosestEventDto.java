package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.EventType;
import com.ratifire.devrate.enums.InterviewRequestRole;
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
  private String roomUrl;
  private long interviewId;
  private InterviewRequestRole role;
  private long hostId;    // opponent userId
  private String title;    // it's mastery level + specialization name of the CANDIDATE request
}
