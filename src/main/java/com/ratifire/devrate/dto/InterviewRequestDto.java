package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.InterviewRequestRole;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for Interview Request.
 */
@Builder
@Getter
public class InterviewRequestDto {

  @NotNull
  private InterviewRequestRole role;
  @NotNull
  private Long masteryId;
  private int desiredInterview;
  @NotNull
  private List<ZonedDateTime> availableDates;
}
