package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for representing interview request details to be displayed on the UI.
 */
@Builder
@Getter
public class InterviewRequestViewDto {

  private long id;
  private InterviewRequestRole role;
  private int desiredInterview;
  private int matchedInterview;
  private String comment;
  private String languageCode;
  private long hardSkillCount;
  private long softSkillCount;
  private List<InterviewRequestTimeSlotDto> timeSlots;
}
