package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the {@link NotificationService} class.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private DataMapper<NotificationDto, Notification> mapper;
  @Mock
  private WebSocketSender webSocketSender;
  @InjectMocks
  private NotificationService notificationService;

  @Test
  void testGetAllByUserId() {
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());

    List<NotificationDto> expectedNotifications = List.of(
        NotificationDto.builder().build(),
        NotificationDto.builder().build());

    when(notificationRepository.findNotificationsByUserId(anyLong())).thenReturn(
        Optional.of(notifications));
    when(mapper.toDto(notifications)).thenReturn(expectedNotifications);

    List<NotificationDto> actualNotifications = notificationService.getAllByUserId(anyLong());

    assertEquals(expectedNotifications, actualNotifications);
  }

  @Test
  void testGetAllByUserId_NotFoundNotifications() {
    when(notificationRepository.findNotificationsByUserId(anyLong())).thenReturn(
        Optional.empty());

    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.getAllByUserId(anyLong()));
  }

  @Test
  void testDelete() {
    notificationService.delete(anyLong());

    verify(notificationRepository, times(1)).deleteById(anyLong());
  }

  @Test
  void testReadNotificationById() {
    long testNotificationId = 1L;
    Notification notification = Notification.builder().build();
    when(notificationRepository.save(notification)).thenReturn(any());
    when(notificationRepository.findById(testNotificationId)).thenReturn(Optional.of(notification));

    notificationService.markAsReadById(testNotificationId);

    assertTrue(notification.isRead());
    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  void testReadNotificationById_NotFound() {
    when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.markAsReadById(anyLong()));
  }

  @Test
  void testSendNotification() {
    long testUserId = 1L;
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User user = User.builder()
        .id(testUserId)
        .notifications(notifications)
        .build();
    NotificationDto expectedNotifications = NotificationDto.builder().build();

    setupSendToUserMock(Notification.builder().build(), expectedNotifications);

    notificationService.addGreeting(user, anyString());
    notificationService.addInterviewRequestExpiry(user, anyString());
    notificationService.addRejectInterview(user, "rejectionUserFirstName", ZonedDateTime.now(),
        anyString());
    notificationService.addInterviewScheduled(user, "role", ZonedDateTime.now(), anyString());
    notificationService.addInterviewFeedbackDetail(user, 1L, anyString());

    verify(notificationRepository, times(5)).save(any());
    verify(webSocketSender, times(5)).sendNotificationByUserEmail(any(), anyString());
  }

  private void setupSendToUserMock(Notification notification,
      NotificationDto expectedNotification) {
    when(notificationRepository.save(any(Notification.class))).thenReturn(any());
    when(mapper.toDto(notification)).thenReturn(expectedNotification);
    doNothing().when(webSocketSender).sendNotificationByUserEmail(any(), anyString());
  }
}
