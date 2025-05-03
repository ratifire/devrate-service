package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for short Interview Request.
 */
@Builder
@Getter
public class InterviewRequestShortDto {

  private long id;
  private InterviewRequestRole role;
  private String specializationName;

}