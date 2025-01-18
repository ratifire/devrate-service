package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the interview event details.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewEventDto {

  private ParticipantDto currentUser;
  private ParticipantDto counterpartUser;

}