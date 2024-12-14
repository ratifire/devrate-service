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
   * Checks if a given level exists in the MasteryLevel enum.
   *
   * @param level the mastery level to check
   * @return {@code true} if the level exists in the enum, {@code false} otherwise
   */
  public static boolean containsLevel(int level) {
    return Stream.of(values()).anyMatch(e -> e.getLevel() == level);
  }
}
