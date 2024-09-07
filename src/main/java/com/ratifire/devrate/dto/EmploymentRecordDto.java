package com.ratifire.devrate.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for employment-record.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmploymentRecordDto {

  private Long id;

  @NotNull
  @Positive
  @Digits(integer = 4, fraction = 0, message = "Provide valid Year in the format YYYY")
  private Integer startYear;

  @NotNull
  @Positive
  @Digits(integer = 4, fraction = 0, message = "Provide valid Year in the format YYYY")
  private Integer endYear;

  @NotBlank
  private String position;

  @NotBlank
  private String companyName;

  private String description;

  private List<String> responsibilities;
}
