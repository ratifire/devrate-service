package com.ratifire.devrate.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * The enum Language proficiency level.
 */

@AllArgsConstructor
public enum LanguageProficiencyLevel {

  BEGINNER_EN("Beginner"),
  PRE_INTERMEDIATE_EN("Pre-Intermediate"),
  INTERMEDIATE_EN("Intermediate"),
  UPPER_INTERMEDIATE_EN("Upper-Intermediate"),
  ADVANCED_EN("Advanced"),
  PROFICIENT_EN("Proficient"),

  ELEMENTARY_UA("Початковий рівень"),
  PRE_INTERMEDIATE_UA("Базовий рівень"),
  INTERMEDIATE_UA("Рубіжний рівень"),
  UPPER_INTERMEDIATE_UA("Середній рівень"),
  ADVANCED_UA("Високий рівень"),
  PROFICIENT_UA("Вільне володіння"),

  BEGINNER_DE("Anfänger"),
  PRE_INTERMEDIATE_DE("Grundlegende Kenntnisse"),
  INTERMEDIATE_DE("Mittelstufe"),
  UPPER_INTERMEDIATE_DE("Selbständige Sprachverwendung"),
  ADVANCED_DE("Fachkundige Sprachkenntnisse"),
  PROFICIENT_DE("Annähernd muttersprachliche Kenntnisse"),

  BEGINNER_FR("Débutant"),
  PRE_INTERMEDIATE_FR("Pré-intermédiaire"),
  INTERMEDIATE_FR("Intermédiaire"),
  UPPER_INTERMEDIATE_FR("Intermédiaire supérieur"),
  ADVANCED_FR("Avancé"),
  PROFICIENT_FR("Courant"),

  BEGINNER_IT("Principiante"),
  PRE_INTERMEDIATE_IT("Pre-intermedio"),
  INTERMEDIATE_IT("Intermedio"),
  UPPER_INTERMEDIATE_IT("Intermedio avanzato"),
  ADVANCED_IT("Avanzato"),
  PROFICIENT_IT("Fluente"),

  BEGINNER_ES("Principiante"),
  PRE_INTERMEDIATE_ES("Pre-intermedio"),
  INTERMEDIATE_ES("Intermedio"),
  UPPER_INTERMEDIATE_ES("Intermedio avanzado"),
  ADVANCED_ES("Avanzado"),
  PROFICIENT_ES("Fluido"),

  BEGINNER_PL("Początkujący"),
  PRE_INTERMEDIATE_PL("Podstawowy"),
  INTERMEDIATE_PL("Średniozaawansowany"),
  UPPER_INTERMEDIATE_PL("Zaawansowany"),
  ADVANCED_PL("Bardzo zaawansowany"),
  PROFICIENT_PL("Biegły"),

  BEGINNER_CS("Začátečník"),
  PRE_INTERMEDIATE_CS("Mírně pokročilý"),
  INTERMEDIATE_CS("Středně pokročilý"),
  UPPER_INTERMEDIATE_CS("Pokročilý"),
  ADVANCED_CS("Velmi pokročilý"),
  PROFICIENT_CS("Plynulý");

  private final String level;

  @JsonValue
  public String getLevel() {
    return this.level;
  }

  /**
   * Returns a list of proficiency levels for a given language code.
   *
   * @param languageCode the language code
   * @return a list of proficiency levels corresponding to the given language code
   */
  public static List<String> getLevelsByLanguage(String languageCode) {
    return Arrays.stream(LanguageProficiencyLevel.values())
        .filter(level -> level.name().endsWith("_" + languageCode))
        .map(LanguageProficiencyLevel::getLevel)
        .toList();
  }

}