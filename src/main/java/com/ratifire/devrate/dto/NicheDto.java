package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data Transfer Object (DTO) for niche.
 */
@Getter
@Builder
@EqualsAndHashCode
public class NicheDto {

  private Long id;

  @NotBlank(message = "Niche name must not be null or empty")
  private String name;

  private boolean main;
}
