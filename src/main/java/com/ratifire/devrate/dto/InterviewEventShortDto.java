package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the interview event short details.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewEventShortDto {

  private ZonedDateTime startTime;
  private String roomUrl;
  private ParticipantDto counterpartUser;

}