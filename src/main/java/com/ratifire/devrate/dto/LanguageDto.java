package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.LanguageLevel;
import com.ratifire.devrate.enums.LanguageName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the user language.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto {

  private long id;

  @NotNull
  private LanguageName name;

  @NotNull
  private LanguageLevel level;

}