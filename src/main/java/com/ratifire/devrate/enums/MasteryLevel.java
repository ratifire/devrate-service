package com.ratifire.devrate.enums;

import java.util.stream.Stream;
import lombok.Getter;

/**
 * Enum representing different mastery levels with associated numeric levels.
 */
@Getter
public enum MasteryLevel {
  JUNIOR(1),
  MIDDLE(2),
  SENIOR(3);

  private final int level;

  /**
   * MasteryLevel.
   *
   * @param level the numeric representation of the mastery level
   */
  MasteryLevel(int level) {
    this.level = level;
  }

  /**
   * Retrieves the numeric level associated with a given mastery name.
   *
   * @param mastery the name of the mastery level (case-insensitive)
   * @return the numeric level corresponding to the given mastery name, or {@code 0} if not found
   */
  public static int getLevelByName(String mastery) {
    return Stream.of(values())
        .filter(level -> level.name().equalsIgnoreCase(mastery))
        .findFirst()
        .map(MasteryLevel::getLevel)
        .orElse(0);
  }

  /**
   * Retrieves the name of the mastery level associated with a given numeric level.
   *
   * @param level the numeric level of the mastery
   * @return the name of the mastery level corresponding to the given numeric level, or "Unknown"
   */
  public static String getNameByLevel(int level) {
    return Stream.of(values())
        .filter(l -> l.getLevel() == level)
        .findFirst()
        .map(MasteryLevel::name)
        .orElse("Unknown");
  }

  /**
   * Checks if a given name exists in the MasteryLevel enum.
   *
   * @param name the name of the mastery level to check (case-insensitive)
   * @return {@code true} if the name exists in the enum, {@code false} otherwise
   */
  public static boolean containsName(String name) {
    return Stream.of(values()).anyMatch(e -> e.name().equalsIgnoreCase(name));
  }
}
