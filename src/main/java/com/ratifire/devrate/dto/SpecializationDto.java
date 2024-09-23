package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for Specialization.
 */
@Getter
@Builder
@EqualsAndHashCode
public class SpecializationDto {

  private Long id;

  @NotBlank(message = "Specialization name must not be null or empty")
  private String name;

  @NotBlank(message = "Main Mastery name must not be null or empty")
  private String mainMasteryName;

  private boolean main;

  private int completedInterviews;

  private int conductedInterviews;
}