package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for work experience.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentRecordDto {

  private Long id;

  @NotNull
  private LocalDate startDate;

  private LocalDate endDate;

  @NotNull
  private String position;

  @NotNull
  private String companyName;

  private String description;

  private List<String> responsibilities;


}
