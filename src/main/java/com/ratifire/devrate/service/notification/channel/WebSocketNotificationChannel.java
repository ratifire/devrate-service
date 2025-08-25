package com.ratifire.devrate.service.notification.channel;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.notification.payload.NotificationPayload;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.service.notification.NotificationChannel;
import com.ratifire.devrate.service.notification.NotificationChannelType;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import com.ratifire.devrate.util.JsonConverter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket notification channel implementation.
 * Handles sending real-time notifications via WebSocket and persisting them in database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketNotificationChannel implements NotificationChannel {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final NotificationRepository notificationRepository;
  private final DataMapper<NotificationDto, Notification> notificationMapper;

  @Value("${notification.websocket.enabled:true}")
  private boolean websocketEnabled;

  @Override
  public boolean send(NotificationRequest request) {
    if (!isAvailable()) {
      log.warn("WebSocket channel is not available");
      return false;
    }

    try {
      Notification notification = null;
      
      // Persist notification in database if required
      if (request.getMetadata().isPersistInDatabase()) {
        notification = createAndSaveNotification(request);
      }

      // Send real-time notification via WebSocket
      sendWebSocketNotification(request, notification);

      return true;

    } catch (Exception e) {
      log.error("Failed to send WebSocket notification to user: {}",
          request.getRecipient().getId(), e);
      return false;
    }
  }

  @Override
  public NotificationChannelType getChannelType() {
    return NotificationChannelType.WEBSOCKET;
  }

  @Override
  public boolean isAvailable() {
    return websocketEnabled && simpMessagingTemplate != null;
  }

  private Notification createAndSaveNotification(NotificationRequest request) {
    Notification notification = Notification.builder()
        .type(request.getType())
        .payload(request.getPayload() != null
            ? JsonConverter.serialize((NotificationPayload) request.getPayload()) : null)
        .read(false)
        .createdAt(request.getMetadata().getCreatedAt() != null
            ? request.getMetadata().getCreatedAt() : LocalDateTime.now(ZoneOffset.UTC))
        .user(request.getRecipient())
        .build();

    return notificationRepository.save(notification);
  }

  private void sendWebSocketNotification(NotificationRequest request, Notification notification) {
    NotificationDto notificationDto;
    
    if (notification != null) {
      notificationDto = notificationMapper.toDto(notification);
    } else {
      // Create DTO directly if not persisting to database
      notificationDto = createNotificationDto(request);
    }

    String destination = String.format("/topic/notifications/%s",
        request.getRecipient().getId());
    simpMessagingTemplate.convertAndSend(destination, notificationDto);
  }

  private NotificationDto createNotificationDto(NotificationRequest request) {
    return NotificationDto.builder()
        .type(request.getType())
        .payload(request.getPayload() != null
            ? JsonConverter.serialize((NotificationPayload) request.getPayload()) : null)
        .read(false)
        .createdAt(request.getMetadata().getCreatedAt() != null
            ? request.getMetadata().getCreatedAt() : LocalDateTime.now(ZoneOffset.UTC))
        .build();
  }
}