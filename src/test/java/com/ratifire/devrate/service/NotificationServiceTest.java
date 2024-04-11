package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
public class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private DataMapper<NotificationDto, Notification> mapper;

  @InjectMocks
  private NotificationService notificationService;

  @Test
  void testGetAllByUserLogin() {
    List<Notification> notifications = List.of(
        Notification.builder().build(),
        Notification.builder().build());
    User testUser = User.builder()
        .notifications(notifications)
        .build();
    UserSecurity testUserSecurity = UserSecurity.builder()
        .user(testUser)
        .build();

    List<NotificationDto> expectedNotifications = List.of(
        NotificationDto.builder().build(),
        NotificationDto.builder().build());

    when(userSecurityService.findByEmail(any())).thenReturn(testUserSecurity);
    when(mapper.toDto(notifications)).thenReturn(expectedNotifications);

    List<NotificationDto> actualNotifications = notificationService.getAllByEmail(any());

    assertEquals(expectedNotifications, actualNotifications);
  }

  @Test
  void testReadNotificationById() {
    long testUserId = 1L;
    Notification notification = Notification.builder().build();
    when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));

    notificationService.markAsReadById(testUserId);

    assertTrue(notification.isRead());
    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  void testReadNotificationById_NotFound() {
    long testUserId = 1L;
    when(notificationRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.markAsReadById(testUserId));
  }

  @Test
  void testDeleteNotificationById() {
    long testUserId = 1L;
    doNothing().when(notificationRepository).deleteById(any());

    notificationService.deleteById(testUserId);

    verify(notificationRepository, times(1)).deleteById(testUserId);
  }

  @Test
  void testSaveNotification() {
    Notification notification = Notification.builder().build();
    when(notificationRepository.save(any())).thenReturn(notification);

    Notification savedNotification = notificationService.save(notification);

    verify(notificationRepository, times(1)).save(notification);
    assertEquals(notification, savedNotification);
  }
}