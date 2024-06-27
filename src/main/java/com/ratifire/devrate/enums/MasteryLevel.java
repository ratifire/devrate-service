package com.ratifire.devrate.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * The enum Mastery level name.
 */
@Getter
public enum MasteryLevel {
  JUNIOR(1),
  MIDDLE(2),
  SENIOR(3);

  private final int level;

  private static final Map<Integer, MasteryLevel> BY_LEVEL = new HashMap<>();

  static {
    for (MasteryLevel e : values()) {
      BY_LEVEL.put(e.level, e);
    }
  }

  MasteryLevel(int level) {
    this.level = level;
  }

  public static MasteryLevel getByLevel(int level) {
    return BY_LEVEL.get(level);
  }
}
