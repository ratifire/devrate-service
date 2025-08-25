package com.ratifire.devrate.service.notification;

import com.ratifire.devrate.service.notification.model.NotificationRequest;

/**
 * Strategy interface for different notification channels.
 * Implements the Strategy pattern to allow different notification delivery methods.
 */
public interface NotificationChannel {

  /**
   * Sends a notification through this channel.
   *
   * @param request The notification request containing all necessary data
   * @return true if notification was sent successfully, false otherwise
   */
  boolean send(NotificationRequest request);

  /**
   * Gets the type of this notification channel.
   *
   * @return The channel type
   */
  NotificationChannelType getChannelType();

  /**
   * Checks if this channel is currently available/enabled.
   *
   * @return true if the channel is available, false otherwise
   */
  boolean isAvailable();
}