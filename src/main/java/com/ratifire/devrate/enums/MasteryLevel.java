package com.ratifire.devrate.enums;

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

  MasteryLevel(int level) {
    this.level = level;
  }

}
