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
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.NotificationType;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryRepository;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.service.notification.factory.NotificationChannelFactory;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import java.math.BigDecimal;
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

  private static final long CANDIDATE_USER_ID = 1L;
  private static final long INTERVIEWER_USER_ID = 2L;
  private static final long EVENT_ID = 1L;
  private static final long CANDIDATE_INTERVIEW_ID = 123L;
  private static final long INTERVIEWER_INTERVIEW_ID = 456L;
  private static final long CANDIDATE_MASTERY_ID = 1L;
  private static final long INTERVIEWER_MASTERY_ID = 2L;
  private static final String INTERVIEWER_FIRST_NAME = "Jane";
  private static final String INTERVIEWER_LAST_NAME = "Smith";
  private static final String SPECIALIZATION_NAME = "Java Development";
  private static final String SKILL_NAME = "Spring Boot";

  @Mock
  private NotificationChannelFactory channelFactory;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private DataMapper<NotificationDto, Notification> notificationMapper;

  @Mock
  private InterviewRepository interviewRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MasteryRepository masteryRepository;

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
    assertEquals(NotificationChannelType.EMAIL, capturedChannelTypes.get(1).get(0));

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
  void shouldSendScheduledNotificationsToInterviewerAndCandidate() {
    // Given
    ZonedDateTime interviewDateTime = ZonedDateTime.now().plusDays(1);

    User interviewer = User.builder()
        .id(INTERVIEWER_USER_ID)
        .firstName(INTERVIEWER_FIRST_NAME)
        .lastName(INTERVIEWER_LAST_NAME)
        .build();
    Specialization candidateSpec = Specialization.builder()
        .id(1L)
        .name(SPECIALIZATION_NAME)
        .build();
    Skill skill = Skill.builder()
        .id(1L)
        .name(SKILL_NAME)
        .build();
    Mastery candidateMastery = Mastery.builder()
        .id(CANDIDATE_MASTERY_ID)
        .specialization(candidateSpec)
        .skills(List.of(skill))
        .build();
    Mastery interviewerMastery = Mastery.builder()
        .id(INTERVIEWER_MASTERY_ID)
        .specialization(candidateSpec)
        .softSkillMark(BigDecimal.valueOf(4.5))
        .hardSkillMark(BigDecimal.valueOf(4.8))
        .build();
    Interview candidateInterview = Interview.builder()
        .id(CANDIDATE_INTERVIEW_ID)
        .userId(CANDIDATE_USER_ID)
        .masteryId(CANDIDATE_MASTERY_ID)
        .role(InterviewRequestRole.CANDIDATE)
        .startTime(interviewDateTime)
        .build();
    Interview interviewerInterview = Interview.builder()
        .id(INTERVIEWER_INTERVIEW_ID)
        .userId(INTERVIEWER_USER_ID)
        .masteryId(INTERVIEWER_MASTERY_ID)
        .role(InterviewRequestRole.INTERVIEWER)
        .startTime(interviewDateTime)
        .build();

    when(interviewRepository.findByEventId(EVENT_ID))
        .thenReturn(List.of(candidateInterview, interviewerInterview));
    when(userRepository.findById(CANDIDATE_USER_ID)).thenReturn(Optional.of(testUser));
    when(userRepository.findById(INTERVIEWER_USER_ID)).thenReturn(Optional.of(interviewer));
    when(masteryRepository.findById(CANDIDATE_MASTERY_ID)).thenReturn(
        Optional.of(candidateMastery));
    when(masteryRepository.findById(INTERVIEWER_MASTERY_ID)).thenReturn(
        Optional.of(interviewerMastery));
    when(channelFactory.getChannels(any())).thenReturn(List.of(mockChannel));
    when(mockChannel.send(any())).thenReturn(true);

    // When
    notificationService.sendInterviewScheduledNotifications(EVENT_ID);

    // Then
    verify(interviewRepository).findByEventId(EVENT_ID);
    verify(userRepository).findById(CANDIDATE_USER_ID);
    verify(userRepository).findById(INTERVIEWER_USER_ID);
    verify(masteryRepository).findById(CANDIDATE_MASTERY_ID);
    verify(masteryRepository).findById(INTERVIEWER_MASTERY_ID);
    verify(channelFactory, times(4)).getChannels(any()); // 2 users × 2 channels each
    verify(mockChannel, times(4)).send(any()); // 2 users × 2 notifications each
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