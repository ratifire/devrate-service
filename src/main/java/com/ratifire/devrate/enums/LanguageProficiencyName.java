package com.ratifire.devrate.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum language proficiency name.
 */
@Getter
@AllArgsConstructor
public enum LanguageProficiencyName {

  UKRAINE("Українська", "UA"),
  ENGLISH("English", "EN"),
  FRENCH("Français", "FR"),
  ITALIAN("Italiano", "IT"),
  GERMAN("Deutsch", "DE"),
  SPANISH("Español", "ES"),
  POLISH("Polski", "PL"),
  CZECH("Čeština", "CS");

  private final String name;
  private final String code;

  @JsonValue
  public String getName() {
    return name;
  }

}