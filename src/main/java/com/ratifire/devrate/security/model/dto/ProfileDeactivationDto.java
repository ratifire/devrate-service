package com.ratifire.devrate.security.model.dto;

import com.ratifire.devrate.dto.InterviewRequestShortDto;
import com.ratifire.devrate.dto.InterviewShortDto;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for user profile deactivation.
 */
@Getter
@Builder
@EqualsAndHashCode
public class ProfileDeactivationDto {

  private List<InterviewRequestShortDto> interviewRequestsToDelete;
  private List<InterviewShortDto> interviewsToDelete;

}