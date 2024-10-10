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
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.util.websocket.WebSocketSender;
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
  private UserSecurityService userSecurityService;
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
  void testDeleteById() {
    notificationService.deleteById(anyLong());

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

    setupSendUserNotificationMock(Notification.builder().build(), user, expectedNotifications);

    notificationService.addGreetingNotification(user);
    notificationService.addInterviewRequestExpiryNotification(user);
    notificationService.addRejectInterview(user, "rejectionUserFirstName", ZonedDateTime.now());
    notificationService.addInterviewScheduled(user, "role", ZonedDateTime.now());
    notificationService.addInterviewFeedbackDetail(user, 1L);

    verify(notificationRepository, times(5)).save(any());
    verify(webSocketSender, times(5)).sendNotificationByUserEmail(any(), anyString());
  }

  private void setupSendUserNotificationMock(Notification notification, User user,
      NotificationDto expectedNotification) {
    when(notificationRepository.save(any(Notification.class))).thenReturn(any());
    when(mapper.toDto(notification)).thenReturn(expectedNotification);
    UserSecurity userSecurity = createTestUserSecurity(user);
    when(userSecurityService.getByUserId(user.getId())).thenReturn(userSecurity);
    doNothing().when(webSocketSender).sendNotificationByUserEmail(any(), anyString());
  }

  private UserSecurity createTestUserSecurity(User user) {
    String email = "test@email.com";
    return UserSecurity.builder()
        .user(user)
        .email(email)
        .build();
  }
}
