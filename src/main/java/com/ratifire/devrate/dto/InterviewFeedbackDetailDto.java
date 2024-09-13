package com.ratifire.devrate.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO class for transferring Feedback Detail about an interview.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InterviewFeedbackDetailDto {

  private ZonedDateTime interviewStartTime;
  private ParticipantDto participant;
  private List<SkillShortDto> skills;

}