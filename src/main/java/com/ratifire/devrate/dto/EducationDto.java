package com.ratifire.devrate.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data transfer object (DTO) representing education.
 */
@Getter
@Builder
@EqualsAndHashCode
public class EducationDto {

  private long id;

  @Size(max = 100)
  @NotBlank(message = "must not be null or empty")
  private String type;

  @Size(max = 100)
  @NotBlank(message = "must not be null or empty")
  private String name;

  @NotBlank(message = "must not be null or empty")
  private String description;

  @NotNull
  @Positive
  @Digits(integer = 4, fraction = 0, message = "Provide valid Year in the format YYYY")
  private Integer startYear;

  @NotNull
  @Positive
  @Digits(integer = 4, fraction = 0, message = "Provide valid Year in the format YYYY")
  private Integer endYear;
}
