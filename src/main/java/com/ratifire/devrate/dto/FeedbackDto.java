package com.ratifire.devrate.dto;

import com.ratifire.devrate.validation.annotations.ValidFeedbackType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) representing the feedback.
 */
@Builder
@Getter
public class FeedbackDto {

  @NotNull
  @ValidFeedbackType
  private String type;

  @NotNull
  @Size(max = 100)
  private String text;
}
