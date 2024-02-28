package com.ratifire.devrate.enums;

/**
 * Enum representing different access levels in the system.
 */
public enum AccessLevel {
  ADMIN("Admin"),
  USER("User");

  /**
   * Returns the default access level.
   *
   * @return The default access level (USER)
   */
  public static AccessLevel getDefault() {
    return USER;
  }

  private final String roleName;

  AccessLevel(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleName() {
    return roleName;
  }
}
