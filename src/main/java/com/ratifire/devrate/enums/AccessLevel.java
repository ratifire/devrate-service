package com.ratifire.devrate.enums;

public enum AccessLevel {
  ADMIN("Admin"),
  USER("User");

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
