package com.ratifire.devrate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO class for transferring feedback data about an interview.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InterviewFeedbackDto {

  @NotNull
  private long interviewId;

  private String feedback;

  @NotNull
  @Valid
  private List<SkillFeedbackDto> skills;

}