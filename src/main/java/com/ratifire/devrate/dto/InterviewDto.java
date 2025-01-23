package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object (DTO) representing the interview details.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InterviewDto {

  private long id;
  private int masteryLevel;
  private String specializationName;
  private ZonedDateTime startTime;
  private InterviewRequestRole role;
  private long hostId;
}