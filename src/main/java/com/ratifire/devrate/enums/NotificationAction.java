package com.ratifire.devrate.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing actions that can be performed on a notification.
 */
public enum NotificationAction {
  READ("read"),
  DELETE("delete");

  private static final Map<String, NotificationAction> BY_VALUE = new HashMap<>();

  static {
    for (NotificationAction actionType : values()) {
      BY_VALUE.put(actionType.value, actionType);
    }
  }

  private final String value;

  NotificationAction(String value) {
    this.value = value;
  }

  /**
   * Retrieves the NotificationAction corresponding to the given string value.
   *
   * @param value The string value of the action.
   * @return The NotificationAction corresponding to the given value, or null if no match is found.
   */
  public static NotificationAction fromValue(String value) {
    return BY_VALUE.get(value.toLowerCase());
  }
}
