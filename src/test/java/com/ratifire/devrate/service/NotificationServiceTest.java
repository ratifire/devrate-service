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

  private final String email = "test@email.com";

  @Test
  void testGetAllByUserLogin() {
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User user = User.builder()
        .notifications(notifications)
        .build();
    UserSecurity testUserSecurity = UserSecurity.builder()
        .user(user)
        .build();

    List<NotificationDto> expectedNotifications = List.of(
        NotificationDto.builder().build(),
        NotificationDto.builder().build());

    when(userSecurityService.findByEmail(any())).thenReturn(testUserSecurity);
    when(notificationRepository.findNotificationsByUserId(anyLong())).thenReturn(
        Optional.of(notifications));
    when(mapper.toDto(notifications)).thenReturn(expectedNotifications);

    List<NotificationDto> actualNotifications = notificationService.getAllByEmail(any());

    assertEquals(expectedNotifications, actualNotifications);
  }

  @Test
  void testGetAllByUserLogin_NotFoundNotifications() {
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User user = User.builder()
        .notifications(notifications)
        .build();
    UserSecurity testUserSecurity = UserSecurity.builder()
        .user(user)
        .build();

    when(userSecurityService.findByEmail(any())).thenReturn(testUserSecurity);
    when(notificationRepository.findNotificationsByUserId(anyLong())).thenReturn(
        Optional.empty());

    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.getAllByEmail(any()));
  }

  @Test
  void testDeleteById() {
    long testUserId = 1L;
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User user = User.builder()
        .id(testUserId)
        .notifications(notifications)
        .build();
    UserSecurity userSecurity  = UserSecurity.builder()
        .user(user)
        .email(email)
        .build();

    List<NotificationDto> expectedNotifications = List.of(
        NotificationDto.builder().build(),
        NotificationDto.builder().build());
    when(userSecurityService.getByUserId(user.getId())).thenReturn(userSecurity);
    when(userSecurityService.findByEmail(userSecurity.getEmail())).thenReturn(userSecurity);
    when(notificationRepository.findNotificationsByUserId(anyLong()))
        .thenReturn(Optional.of(notifications));
    when(mapper.toDto(notifications)).thenReturn(expectedNotifications);
    doNothing().when(webSocketSender).sendNotificationsByUserEmail(any(), anyString());

    notificationService.deleteById(user.getId(), 1L);

    verify(notificationRepository, times(1)).deleteById(user.getId());
    verify(webSocketSender, times(1)).sendNotificationsByUserEmail(expectedNotifications,
        email);
  }

  @Test
  void testReadNotificationById() {
    long testUserId = 1L;
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User user = User.builder()
        .id(testUserId)
        .notifications(notifications)
        .build();
    List<NotificationDto> expectedNotifications = List.of(
        NotificationDto.builder().build(),
        NotificationDto.builder().build());

    setupSendUserNotificationsMock(notifications, user, expectedNotifications);
    Notification notification = Notification.builder().build();
    when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));

    notificationService.markAsReadById(user.getId(), 1L);

    assertTrue(notification.isRead());
    verify(notificationRepository, times(1)).save(notification);
    verify(webSocketSender, times(1)).sendNotificationsByUserEmail(expectedNotifications,
        email);
  }

  @Test
  void testReadNotificationById_NotFound() {
    long testUserId = 1L;
    when(notificationRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.markAsReadById(testUserId, 1L));
  }

  @Test
  void testAddGreetingNotification() {
    long testUserId = 1L;
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User user = User.builder()
        .id(testUserId)
        .notifications(notifications)
        .build();
    List<NotificationDto> expectedNotifications = List.of(
        NotificationDto.builder().build(),
        NotificationDto.builder().build());

    setupSendUserNotificationsMock(notifications, user, expectedNotifications);

    notificationService.addGreetingNotification(user);
    notificationService.addInterviewRequestExpiryNotification(user);
    notificationService.addRejectInterview(user, "rejectionUserFirstName", ZonedDateTime.now());
    notificationService.addInterviewScheduled(user, "role", ZonedDateTime.now());
    notificationService.addInterviewFeedbackDetail(user, 1L);

    verify(notificationRepository, times(5)).save(any());
    verify(webSocketSender, times(5)).sendNotificationsByUserEmail(any(), anyString());
  }

  private void setupSendUserNotificationsMock(List<Notification> notifications, User user,
      List<NotificationDto> expectedNotifications) {
    when(notificationRepository.save(any(Notification.class))).thenReturn(any());
    UserSecurity userSecurity = createTestUserSecurity(user);
    when(userSecurityService.getByUserId(user.getId())).thenReturn(userSecurity);
    when(userSecurityService.findByEmail(userSecurity.getEmail())).thenReturn(userSecurity);
    when(notificationRepository.findNotificationsByUserId(anyLong()))
        .thenReturn(Optional.of(notifications));
    when(mapper.toDto(notifications)).thenReturn(expectedNotifications);
    doNothing().when(webSocketSender).sendNotificationsByUserEmail(any(), anyString());
  }

  private UserSecurity createTestUserSecurity(User user) {
    return UserSecurity.builder()
        .user(user)
        .email(email)
        .build();
  }
}
