package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the user language proficiency.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageProficiencyDto {

  private long id;

  @NotNull
  private String name;

  @NotNull
  private String code;

  @NotNull
  private String level;

}