package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the InterviewHistory.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewHistoryDto {

  private long id;

  private ZonedDateTime dateTime;

  private long duration;

  private long userId;

  private Map<String, Integer> softSkills;

  private Map<String, Integer> hardSkills;

  private String title;

  private InterviewRequestRole role;

  private int attendeeId;

  private String feedback;

}
