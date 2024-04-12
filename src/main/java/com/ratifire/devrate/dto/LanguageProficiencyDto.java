package com.ratifire.devrate.dto;

import com.ratifire.devrate.enums.LanguageProficiencyLevel;
import com.ratifire.devrate.enums.LanguageProficiencyName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing the user language proficiency.
 */
@Builder
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageProficiencyDto {

  private long id;

  @NotNull
  private LanguageProficiencyName name;

  @NotNull
  private LanguageProficiencyLevel level;

}