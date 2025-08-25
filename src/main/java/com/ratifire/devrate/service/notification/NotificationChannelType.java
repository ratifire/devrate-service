package com.ratifire.devrate.service.notification;

/**
 * Enumeration of supported notification channel types.
 */
public enum NotificationChannelType {
  EMAIL("email"),
  WEBSOCKET("websocket"),
  WEB_PUSH("web_push"),
  SMS("sms");

  private final String channelName;

  NotificationChannelType(String channelName) {
    this.channelName = channelName;
  }

  public String getChannelName() {
    return channelName;
  }
}