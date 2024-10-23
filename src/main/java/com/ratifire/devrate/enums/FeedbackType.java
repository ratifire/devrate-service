package com.ratifire.devrate.enums;

import java.util.List;

/**
 * Enum representing feedback types.
 */
public enum FeedbackType {
  PROPOSITION,
  ISSUE,
  FEEDBACK;

  public static List<String> getAll() {
    return List.of(PROPOSITION.name(), ISSUE.name(), FEEDBACK.name());
  }
}
