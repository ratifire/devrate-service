package com.ratifire.devrate.dto;

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
  private String type;

  @NotNull
  @Size(max = 300)
  private String text;
}
