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
  private String status;    // name of specialization for interview request
  private String level;     // mastery level for interview request
  private InterviewRequestRole role;
}