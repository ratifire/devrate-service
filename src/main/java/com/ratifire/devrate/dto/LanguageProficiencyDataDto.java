package com.ratifire.devrate.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data Transfer Object representing the language proficiency data.
 */
@Getter
@AllArgsConstructor
public class LanguageProficiencyDataDto {

  private String name;
  private String code;
  private List<String> levels;

}
