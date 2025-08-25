package com.ratifire.devrate.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.NotificationType;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.service.notification.factory.NotificationChannelFactory;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the new {@link NotificationService} class.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationChannelFactory channelFactory;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private DataMapper<NotificationDto, Notification> notificationMapper;

  @Mock
  private NotificationChannel mockChannel;

  @InjectMocks
  private NotificationService notificationService;

  @Captor
  private ArgumentCaptor<NotificationRequest> requestCaptor;

  @Captor
  private ArgumentCaptor<List<NotificationChannelType>> channelTypesCaptor;

  private User testUser;
  private Notification testNotification;
  private NotificationDto testNotificationDto;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(1L)
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .build();

    testNotification = Notification.builder()
        .id(1L)
        .type(NotificationType.GREETING)
        .read(false)
        .createdAt(LocalDateTime.now())
        .user(testUser)
        .build();

    testNotificationDto = NotificationDto.builder()
        .id(1L)
        .type(NotificationType.GREETING)
        .read(false)
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void testSendGreeting() {
    // Given
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendGreeting(testUser);

    // Then
    verify(channelFactory, times(2)).getChannels(channelTypesCaptor.capture());
    
    List<List<NotificationChannelType>> capturedChannelTypes = channelTypesCaptor.getAllValues();
    // First call: WebSocket and WebPush
    assertEquals(2, capturedChannelTypes.get(0).size());
    assertTrue(capturedChannelTypes.get(0)
        .containsAll(List.of(NotificationChannelType.WEBSOCKET, NotificationChannelType.WEB_PUSH)));
    
    // Second call: Email
    assertEquals(1, capturedChannelTypes.get(1).size());
    assertEquals(NotificationChannelType.EMAIL, capturedChannelTypes.get(1).getFirst());
    
    verify(mockChannel, times(2)).send(any());
  }

  @Test
  void testSendGreeting_NoChannelsAvailable() {
    // Given
    when(channelFactory.getChannels(any())).thenReturn(List.of());

    // When
    notificationService.sendGreeting(testUser);

    // Then
    verify(mockChannel, times(0)).send(any());
  }

  @Test
  void testSendInterviewRequestExpiry() {
    // Given
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendInterviewRequestExpiry(testUser);

    // Then
    verify(channelFactory, times(2)).getChannels(any());
    verify(mockChannel, times(2)).send(requestCaptor.capture());
    
    List<NotificationRequest> capturedRequests = requestCaptor.getAllValues();
    
    // First request should be in-app notification
    assertEquals(testUser, capturedRequests.get(0).getRecipient());
    assertEquals(NotificationType.INTERVIEW_REQUEST_EXPIRED, capturedRequests.get(0).getType());
    
    // Second request should be email notification
    assertEquals(testUser, capturedRequests.get(1).getRecipient());
    assertEquals("Interview Request Expired", capturedRequests.get(1).getSubject());
  }

  @Test
  void testSendInterviewRejection() {
    // Given
    User rejectionUser = User.builder()
        .id(2L)
        .firstName("Jane")
        .lastName("Smith")
        .build();
    
    ZonedDateTime scheduledTime = ZonedDateTime.now();
    long interviewId = 123L;
    
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendInterviewRejection(testUser, rejectionUser, scheduledTime, interviewId);

    // Then
    verify(channelFactory, times(2)).getChannels(any());
    verify(mockChannel, times(2)).send(requestCaptor.capture());
    
    List<NotificationRequest> capturedRequests = requestCaptor.getAllValues();
    
    // First request should be in-app notification
    assertEquals(testUser, capturedRequests.get(0).getRecipient());
    assertEquals(NotificationType.INTERVIEW_REJECTED, capturedRequests.get(0).getType());
    
    // Second request should be email notification
    assertEquals(testUser, capturedRequests.get(1).getRecipient());
    assertEquals("Interview Rejected", capturedRequests.get(1).getSubject());
    assertEquals("interview-rejected-email", capturedRequests.get(1).getContent());
  }

  @Test
  void testSendInterviewScheduled() {
    // Given
    ZonedDateTime interviewDateTime = ZonedDateTime.now().plusDays(1);
    InterviewRequest interviewRequest = InterviewRequest.builder()
        .id(1L)
        .role(InterviewRequestRole.CANDIDATE)
        .build();
    long interviewId = 456L;
    String role = "CANDIDATE";
    
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendInterviewScheduled(testUser, role, interviewDateTime, 
        interviewRequest, interviewId);

    // Then
    verify(channelFactory, times(2)).getChannels(any());
    verify(mockChannel, times(2)).send(requestCaptor.capture());
    
    List<NotificationRequest> capturedRequests = requestCaptor.getAllValues();
    
    // First request should be in-app notification
    assertEquals(testUser, capturedRequests.get(0).getRecipient());
    assertEquals(NotificationType.INTERVIEW_SCHEDULED, capturedRequests.get(0).getType());
    
    // Second request should be email notification
    assertEquals(testUser, capturedRequests.get(1).getRecipient());
    assertEquals("Interview Scheduled Successfully", capturedRequests.get(1).getSubject());
    assertEquals("interviewer-interview-scheduled-email", capturedRequests.get(1).getContent());
  }

  @Test
  void testSendNotification_Success() {
    // Given
    NotificationRequest request = NotificationRequest.forInAppNotification(
        testUser, NotificationType.GREETING, null, 
        com.ratifire.devrate.service.notification.model.NotificationMetadata.defaultMetadata());
    
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendNotification(request, NotificationChannelType.WEBSOCKET);

    // Then
    verify(channelFactory).getChannels(List.of(NotificationChannelType.WEBSOCKET));
    verify(mockChannel).send(request);
  }

  @Test
  void testSendNotification_ChannelSendFails() {
    // Given
    NotificationRequest request = NotificationRequest.forInAppNotification(
        testUser, NotificationType.GREETING, null, 
        com.ratifire.devrate.service.notification.model.NotificationMetadata.defaultMetadata());
    
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(false);

    // When
    notificationService.sendNotification(request, NotificationChannelType.WEBSOCKET);

    // Then
    verify(mockChannel).send(request);
    // Should still attempt to send even if it fails
  }

  @Test
  void testSendPasswordChangeConfirmation() {
    // Given
    String email = "test@example.com";
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendPasswordChangeConfirmation(email);

    // Then
    verify(channelFactory).getChannels(List.of(NotificationChannelType.EMAIL));
    verify(mockChannel).send(requestCaptor.capture());

    NotificationRequest capturedRequest = requestCaptor.getValue();
    assertEquals(email, capturedRequest.getRecipient().getEmail());
    assertEquals("Password Successfully Reset", capturedRequest.getSubject());
    assertTrue(
        capturedRequest.getContent().contains("Your password has been successfully changed"));
  }

  @Test
  void testGetAllByUserId() {
    // Given
    long userId = 1L;
    List<Notification> notifications = List.of(testNotification);
    List<NotificationDto> expectedDtos = List.of(testNotificationDto);
    
    when(notificationRepository.findNotificationsByUserIdOrderByCreatedAtDesc(userId))
        .thenReturn(Optional.of(notifications));
    when(notificationMapper.toDto(notifications)).thenReturn(expectedDtos);

    // When
    List<NotificationDto> result = notificationService.getAllByUserId(userId);

    // Then
    assertEquals(expectedDtos, result);
    verify(notificationRepository).findNotificationsByUserIdOrderByCreatedAtDesc(userId);
    verify(notificationMapper).toDto(notifications);
  }

  @Test
  void testGetAllByUserId_NotFound() {
    // Given
    long userId = 1L;
    when(notificationRepository.findNotificationsByUserIdOrderByCreatedAtDesc(userId))
        .thenReturn(Optional.empty());

    // When & Then
    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.getAllByUserId(userId));
    
    verify(notificationRepository).findNotificationsByUserIdOrderByCreatedAtDesc(userId);
    verifyNoInteractions(notificationMapper);
  }

  @Test
  void testMarkAsReadById() {
    // Given
    long notificationId = 1L;
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
    when(notificationRepository.save(testNotification)).thenReturn(testNotification);

    // When
    notificationService.markAsReadById(notificationId);

    // Then
    assertTrue(testNotification.isRead());
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository).save(testNotification);
  }

  @Test
  void testMarkAsReadById_NotFound() {
    // Given
    long notificationId = 1L;
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(NotificationNotFoundException.class,
        () -> notificationService.markAsReadById(notificationId));
    
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository, times(0)).save(any());
  }

  @Test
  void testDeleteById() {
    // Given
    long notificationId = 1L;

    // When
    notificationService.deleteById(notificationId);

    // Then
    verify(notificationRepository).deleteById(notificationId);
  }
}