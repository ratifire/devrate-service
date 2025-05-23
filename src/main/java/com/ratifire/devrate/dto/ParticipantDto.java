package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the participant data of event.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
  private long id;
  private String name;
  private String surname;
  private int masteryLevel;
  private String specializationName;
  private InterviewRequestRole role;
}