package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for work experience.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceDto {

  private Long id;

  @NotNull
  private LocalDate startDate;

  @NotNull
  private LocalDate endDate;

  @NotNull
  private String position;

  @NotNull
  private String companyName;

  private String description;

  private String responsibilityId;

  private long userId;

}
