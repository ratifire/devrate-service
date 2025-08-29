package com.ratifire.devrate.service.notification;

import static com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE;
import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Notification;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.notification.payload.InterviewRejectedPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewRequestExpiredPayload;
import com.ratifire.devrate.entity.notification.payload.InterviewScheduledPayload;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.NotificationType;
import com.ratifire.devrate.exception.NotificationNotFoundException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryRepository;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.service.notification.factory.NotificationChannelFactory;
import com.ratifire.devrate.service.notification.model.NotificationMetadata;
import com.ratifire.devrate.service.notification.model.NotificationRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Unified notification service that acts as a facade for all notification operations. Implements
 * the Facade pattern to provide a simple interface for complex notification logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSS";
  private static final String KIEV_TIMEZONE = "Europe/Kyiv";
  private static final int INTERVIEW_DURATION_MINUTES = 60;
  private static final String CANDIDATE_EMAIL_TEMPLATE = "candidate-interview-scheduled-email";
  private static final String INTERVIEWER_EMAIL_TEMPLATE =
      "interviewer-interview-scheduled-email.html";

  private final NotificationChannelFactory channelFactory;
  private final NotificationRepository notificationRepository;
  private final DataMapper<NotificationDto, Notification> notificationMapper;
  private final InterviewRepository interviewRepository;
  private final UserRepository userRepository;
  private final MasteryRepository masteryRepository;

  /**
   * Sends a greeting notification to a new user via multiple channels.
   *
   * @param user The user to send greeting to
   */
  public void sendGreeting(User user) {
    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        user, NotificationType.GREETING, null, NotificationMetadata.defaultMetadata());

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        user, "Welcome to DevRate!", "greeting-email",
        Map.of("user", user), NotificationMetadata.defaultMetadata());

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends an interview request expiration notification.
   *
   * @param user The user whose interview request expired
   */
  public void sendInterviewRequestExpiry(User user) {
    InterviewRequestExpiredPayload payload = InterviewRequestExpiredPayload.builder()
        .userFirstName(user.getFirstName())
        .build();

    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        user, NotificationType.INTERVIEW_REQUEST_EXPIRED, payload,
        NotificationMetadata.defaultMetadata());

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        user, "Interview Request Expired", "interview-request-expired-email",
        Map.of("user", user), NotificationMetadata.defaultMetadata());

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends an interview rejection notification.
   *
   * @param recipient The user who will receive the rejection notification
   * @param rejectionUser The user who rejected the interview
   * @param scheduledTime The scheduled time of the rejected interview
   * @param recipientInterviewId The interview ID for the recipient
   */
  public void sendInterviewRejection(User recipient, User rejectionUser,
      ZonedDateTime scheduledTime, long recipientInterviewId) {

    NotificationMetadata metadata = NotificationMetadata.withRejection(
        rejectionUser.getFirstName(), scheduledTime, recipientInterviewId);

    InterviewRejectedPayload payload = InterviewRejectedPayload.builder()
        .rejectionName(rejectionUser.getFirstName())
        .scheduledDateTime(
            scheduledTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .rejectedInterviewId(String.valueOf(recipientInterviewId))
        .build();

    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(
        recipient, NotificationType.INTERVIEW_REJECTED, payload, metadata);

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    Map<String, Object> emailVariables = new HashMap<>();
    emailVariables.put("recipientUser", recipient);
    emailVariables.put("rejectionUser", rejectionUser);
    emailVariables.put("scheduledTime",
        scheduledTime.withZoneSameInstant(ZoneId.of("Europe/Kyiv")));

    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(
        recipient, "Interview Rejected", "interview-rejected-email",
        emailVariables, metadata);

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  /**
   * Sends interview scheduled notifications to both candidate and interviewer.
   *
   * @param eventId the event ID containing interview details
   */
  public void sendInterviewScheduledNotifications(long eventId) {
    try {
      List<Interview> interviews = interviewRepository.findByEventId(eventId);

      ZonedDateTime date = interviews.getFirst().getStartTime();

      Map<InterviewRequestRole, Interview> roleToInterview = interviews.stream()
          .collect(Collectors.toMap(Interview::getRole, interview -> interview));

      Interview candidateInterview = roleToInterview.get(CANDIDATE);
      long candidateUserId = candidateInterview.getUserId();
      User candidate = userRepository.findById(candidateUserId)
          .orElseThrow(() -> new UserNotFoundException(candidateUserId));
      long candidateMasteryId = candidateInterview.getMasteryId();
      Mastery candidateMastery = masteryRepository.findById(candidateMasteryId).orElseThrow(
          () -> new ResourceNotFoundException("Mastery not found with id: " + candidateMasteryId));

      Interview interviewerInterview = roleToInterview.get(INTERVIEWER);
      long interviewerUserId = interviewerInterview.getUserId();
      User interviewer = userRepository.findById(interviewerUserId)
          .orElseThrow(() -> new UserNotFoundException(interviewerUserId));
      long interviewerMasteryId = interviewerInterview.getMasteryId();
      Mastery interviewerMastery = masteryRepository.findById(interviewerMasteryId).orElseThrow(
          () -> new ResourceNotFoundException(
              "Mastery not found with id: " + interviewerMasteryId));

      notifyCandidateAboutSchedule(candidate, candidateInterview.getId(), date, CANDIDATE.name(),
          interviewer.getFirstName(), interviewer.getLastName(),
          interviewerMastery.getSpecialization().getName(), interviewerMastery.getSoftSkillMark(),
          interviewerMastery.getHardSkillMark());

      notifyInterviewerAboutSchedule(interviewer, interviewerInterview.getId(), date,
          INTERVIEWER.name(), candidate.getFirstName(), candidate.getLastName(),
          candidateMastery.getSpecialization().getName(),
          candidateMastery.getSkills().stream().map(Skill::getName).toList());

    } catch (Exception e) {
      log.error("Failed to send interview scheduled notifications for matched users. EventId: {}",
          eventId, e);
    }
  }

  private void notifyCandidateAboutSchedule(User candidate, long candidateInterviewId,
      ZonedDateTime interviewDateTime, String role, String interviewerFirstName,
      String interviewerLastName, String interviewerSpecialization,
      BigDecimal interviewerSoftSkillMark, BigDecimal interviewerHardSkillMark) {

    Map<String, Object> emailVariables = createCandidateInterviewScheduledEmailVariables(candidate,
        interviewDateTime, interviewerFirstName, interviewerLastName, interviewerSpecialization,
        interviewerSoftSkillMark, interviewerHardSkillMark);

    notifyAboutScheduledInterview(candidate, candidateInterviewId, interviewDateTime, role,
        CANDIDATE_EMAIL_TEMPLATE, emailVariables);
  }

  private Map<String, Object> createCandidateInterviewScheduledEmailVariables(User recipient,
      ZonedDateTime interviewDateTime, String interviewerFirstName, String interviewerLastName,
      String interviewerSpecialization, BigDecimal interviewerSoftSkillMark,
      BigDecimal interviewerHardSkillMark) {

    Map<String, Object> variables = createBaseEmailVariables(recipient, interviewDateTime);
    variables.put("interviewer_first_name", interviewerFirstName);
    variables.put("interviewer_last_name", interviewerLastName);
    variables.put("interviewer_specialization", interviewerSpecialization);
    variables.put("interviewer_soft_skill_mark", interviewerSoftSkillMark);
    variables.put("interviewer_hard_skill_mark", interviewerHardSkillMark);

    return variables;
  }

  private void notifyInterviewerAboutSchedule(User interviewer, long interviewerInterviewId,
      ZonedDateTime interviewDateTime, String role, String candidateFirstName,
      String candidateLastName, String candidateSpecialization, List<String> candidateSkills) {

    Map<String, Object> emailVariables = createInterviewerInterviewScheduledEmailVariables(
        interviewer, interviewDateTime, candidateFirstName, candidateLastName,
        candidateSpecialization, candidateSkills);

    notifyAboutScheduledInterview(interviewer, interviewerInterviewId, interviewDateTime, role,
        INTERVIEWER_EMAIL_TEMPLATE, emailVariables);
  }

  /**
   * Unified method to send interview schedule notifications (in-app and email).
   */
  private void notifyAboutScheduledInterview(User recipient, long interviewId,
      ZonedDateTime startTime, String role, String templateName,
      Map<String, Object> emailVariables) {
    NotificationMetadata metadata = NotificationMetadata.withInterview(interviewId,
        startTime, role);

    InterviewScheduledPayload payload = InterviewScheduledPayload.builder().role(role)
        .scheduledDateTime(
            startTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
        .interviewId(interviewId).build();

    // In-app notification
    NotificationRequest inAppRequest = NotificationRequest.forInAppNotification(recipient,
        NotificationType.INTERVIEW_SCHEDULED, payload, metadata);

    sendNotification(inAppRequest, NotificationChannelType.WEBSOCKET,
        NotificationChannelType.WEB_PUSH);

    // Email notification
    NotificationRequest emailRequest = NotificationRequest.forEmailNotification(recipient,
        "Interview Scheduled Successfully", templateName, emailVariables, metadata);

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }

  private Map<String, Object> createInterviewerInterviewScheduledEmailVariables(User recipient,
      ZonedDateTime interviewDateTime, String candidateFirstName, String candidateLastName,
      String candidateSpecialization, List<String> candidateSkills) {

    Map<String, Object> variables = createBaseEmailVariables(recipient, interviewDateTime);
    variables.put("candidate_first_name", candidateFirstName);
    variables.put("candidate_last_name", candidateLastName);
    variables.put("candidate_specialization", candidateSpecialization);
    variables.put("candidate_skills", candidateSkills);

    return variables;
  }

  /**
   * Creates base email variables common to all interview scheduled notifications.
   */
  private Map<String, Object> createBaseEmailVariables(User recipient,
      ZonedDateTime interviewDateTime) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("recipient_first_name", recipient.getFirstName());
    variables.put("interview_datetime",
        interviewDateTime.withZoneSameInstant(ZoneId.of(KIEV_TIMEZONE)));

    // Google Calendar URL
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    String googleCalendarUrl =
        "https://www.google.com/calendar/render?action=TEMPLATE" + "&text=" + URLEncoder.encode(
            "Інтерв'ю Skillzzy", StandardCharsets.UTF_8) + "&dates=" + formatter.format(
            interviewDateTime) + "/" + formatter.format(
            interviewDateTime.plusMinutes(INTERVIEW_DURATION_MINUTES));

    variables.put("google_calendar_url", googleCalendarUrl);
    return variables;
  }

  /**
   * Sends a notification through specified channels.
   *
   * @param request      The notification request
   * @param channelTypes The channels to send through
   */
  public void sendNotification(NotificationRequest request,
      NotificationChannelType... channelTypes) {
    List<NotificationChannel> channels = channelFactory.getChannels(List.of(channelTypes));

    if (channels.isEmpty()) {
      log.warn("No available channels found for notification to user: {}",
          request.getRecipient().getId());
      return;
    }

    for (NotificationChannel channel : channels) {
      try {
        if (!channel.send(request)) {
          log.warn("Failed to send notification via {}", channel.getChannelType());
        }
      } catch (Exception e) {
        log.error("Error sending notification via {}", channel.getChannelType(), e);
      }
    }
  }

  /**
   * Retrieves all notifications for a user.
   *
   * @param userId The user ID
   * @return List of notification DTOs
   */
  public List<NotificationDto> getAllByUserId(long userId) {
    List<Notification> notifications = notificationRepository
        .findNotificationsByUserIdOrderByCreatedAtDesc(userId)
        .orElseThrow(() -> new NotificationNotFoundException(
            "Can not find notifications by user id " + userId));

    return notificationMapper.toDto(notifications);
  }

  /**
   * Marks a notification as read.
   *
   * @param notificationId The notification ID
   */
  public void markAsReadById(long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));

    notification.setRead(true);
    notificationRepository.save(notification);
  }

  /**
   * Deletes a notification.
   *
   * @param notificationId The notification ID
   */
  public void deleteById(long notificationId) {
    notificationRepository.deleteById(notificationId);
  }

  /**
   * Sends a password change confirmation notification via email.
   *
   * @param email The user's email address
   */
  public void sendPasswordChangeConfirmation(String email) {
    // Create a minimal user object for email sending
    User emailUser = User.builder().email(email).build();

    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String content = "Your password has been successfully changed on " + formattedDateTime + ".";

    NotificationRequest emailRequest = NotificationRequest.builder()
        .recipient(emailUser)
        .subject("Password Successfully Reset")
        .content(content) // Plain text content instead of template
        .metadata(NotificationMetadata.builder()
            .createdAt(now)
            .persistInDatabase(false)
            .build())
        .build();

    sendNotification(emailRequest, NotificationChannelType.EMAIL);
  }
}