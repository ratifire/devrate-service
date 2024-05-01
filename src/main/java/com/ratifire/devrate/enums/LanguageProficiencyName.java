package com.ratifire.devrate.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The enum language proficiency name.
 */
public enum LanguageProficiencyName {

  UKRAINE("UA"),
  ENGLISH("EN"),
  FRENCH("FR"),
  ITALIAN("IT"),
  GERMAN("DE"),
  SPANISH("ES"),
  POLISH("PL"),
  CZECH("CS");

  private final String code;

  LanguageProficiencyName(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return this.code;
  }
}