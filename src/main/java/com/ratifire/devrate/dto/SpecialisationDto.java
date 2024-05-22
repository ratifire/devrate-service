package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for Specialisation.
 */
@Getter
@Builder
@EqualsAndHashCode
public class SpecialisationDto {

  private Long id;

  @NotNull
  private String name;
}
