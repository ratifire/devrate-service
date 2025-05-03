package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the short interview details.
 */
@Builder
@Getter
@EqualsAndHashCode
public class InterviewShortDto {

  private long id;
  private int masteryLevel;
  private String specializationName;
  private ZonedDateTime startTime;
  private InterviewRequestRole role;

}