package com.ratifire.devrate.service.notification.factory;

import com.ratifire.devrate.service.notification.NotificationChannel;
import com.ratifire.devrate.service.notification.NotificationChannelType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for creating and managing notification channels.
 * Implements the Factory pattern to provide notification channels based on type.
 */
@Slf4j
@Component
public class NotificationChannelFactory {

  private final Map<NotificationChannelType, NotificationChannel> channelMap;

  /**
   * Constructs a NotificationChannelFactory with the provided notification channels.
   * Automatically discovers all NotificationChannel beans and maps them by their channel type.
   *
   * @param channels List of notification channel implementations injected by Spring
   */
  public NotificationChannelFactory(List<NotificationChannel> channels) {
    this.channelMap = channels.stream()
        .collect(Collectors.toMap(
            NotificationChannel::getChannelType,
            Function.identity()
        ));
    
    log.info("Initialized NotificationChannelFactory with {} channels: {}",
        channelMap.size(), channelMap.keySet());
  }

  /**
   * Gets a notification channel by type.
   *
   * @param type The channel type
   * @return Optional containing the channel if found and available
   */
  public Optional<NotificationChannel> getChannel(NotificationChannelType type) {
    NotificationChannel channel = channelMap.get(type);
    
    if (channel == null) {
      log.warn("No channel found for type: {}", type);
      return Optional.empty();
    }
    
    if (!channel.isAvailable()) {
      log.warn("Channel {} is not available", type);
      return Optional.empty();
    }
    
    return Optional.of(channel);
  }

  /**
   * Gets all available notification channels.
   *
   * @return List of available channels
   */
  public List<NotificationChannel> getAvailableChannels() {
    return channelMap.values().stream()
        .filter(NotificationChannel::isAvailable)
        .collect(Collectors.toList());
  }

  /**
   * Gets channels by multiple types.
   *
   * @param types The channel types to retrieve
   * @return List of available channels matching the types
   */
  public List<NotificationChannel> getChannels(List<NotificationChannelType> types) {
    return types.stream()
        .map(this::getChannel)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  /**
   * Checks if a specific channel type is available.
   *
   * @param type The channel type to check
   * @return true if the channel is available, false otherwise
   */
  public boolean isChannelAvailable(NotificationChannelType type) {
    return getChannel(type).isPresent();
  }
}