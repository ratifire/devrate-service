package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for Specialisation.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SpecialisationNameDto {

  private Long id;

  @NotNull
  private String specialisationName;
}
