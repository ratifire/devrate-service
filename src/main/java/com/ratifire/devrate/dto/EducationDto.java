package com.ratifire.devrate.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object (DTO) representing education.
 */
@Getter
@Builder
public class EducationDto {

  private long id;

  @NotEmpty
  @Size(max = 100)
  private String type;

  @NotEmpty
  @Size(max = 100)
  private String name;

  @NotEmpty
  private String description;

  @Digits(integer = 4, fraction = 0, message = "Provide valid Year in the format YYYY")
  @PositiveOrZero
  private int startYear;

  @Digits(integer = 4, fraction = 0, message = "Provide valid Year in the format YYYY")
  @PositiveOrZero
  private int endYear;
}
