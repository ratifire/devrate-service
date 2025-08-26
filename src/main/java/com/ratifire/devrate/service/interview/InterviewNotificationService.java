package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.PairedParticipantDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.notification.NotificationService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that handles notification coordination for interview operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewNotificationService {

  private final InterviewService interviewService;
  private final InterviewRequestService interviewRequestService;
  private final NotificationService notificationService;
  private final UserService userService;
  private final UserContextProvider userContextProvider;

  /**
   * Creates interviews and sends notifications after successful transaction commit.
   */
  public void createInterviewWithNotifications(PairedParticipantDto matchedUsers) {
    List<Interview> createdInterviews = interviewService.create(matchedUsers);
    sendInterviewScheduledNotifications(matchedUsers, createdInterviews);
  }

  /**
   * Deletes a rejected interview and sends notifications after successful transaction commit.
   */
  public void deleteRejectedInterviewWithNotifications(long id) {
    List<Interview> deletedInterviews = interviewService.deleteRejected(id);
    sendInterviewRejectionNotifications(deletedInterviews);
  }

  /**
   * Sends interview scheduled notifications.
   */
  public void sendInterviewScheduledNotifications(PairedParticipantDto matchedUsers,
      List<Interview> interviews) {
    try {
      long interviewerRequestId = matchedUsers.getInterviewerParticipantId();
      long candidateRequestId = matchedUsers.getCandidateParticipantId();
      ZonedDateTime date = matchedUsers.getDate();

      List<InterviewRequest> requests = interviewRequestService.findByIds(
          List.of(interviewerRequestId, candidateRequestId));

      Map<Long, InterviewRequest> interviewRequestById = requests.stream()
          .collect(Collectors.toMap(InterviewRequest::getId, request -> request));

      sendInterviewScheduledAlerts(
          interviewRequestById.get(interviewerRequestId),
          interviewRequestById.get(candidateRequestId),
          date,
          interviews);
    } catch (Exception e) {
      log.error("Failed to send interview scheduled notifications for matched users: {}",
          matchedUsers, e);
    }
  }

  /**
   * Sends alerts to both the interviewer and candidate about the scheduled interview.
   */
  private void sendInterviewScheduledAlerts(InterviewRequest interviewerRequest,
      InterviewRequest candidateRequest, ZonedDateTime date, List<Interview> interviews) {

    Map<InterviewRequestRole, Long> roleToId = interviews.stream()
        .collect(Collectors.toMap(Interview::getRole, Interview::getId));

    notifyParticipantAboutSchedule(candidateRequest, interviewerRequest, date,
        roleToId.get(InterviewRequestRole.CANDIDATE));
    notifyParticipantAboutSchedule(interviewerRequest, candidateRequest, date,
        roleToId.get(InterviewRequestRole.INTERVIEWER));
  }

  /**
   * Sends a notification and an email to a participant of an interview about the scheduled
   * interview.
   */
  private void notifyParticipantAboutSchedule(InterviewRequest recipientRequest,
      InterviewRequest secondParticipantRequest, ZonedDateTime interviewStartTimeInUtc,
      long interviewId) {

    User recipient = recipientRequest.getUser();
    String role = String.valueOf(recipientRequest.getRole());

    notificationService.sendInterviewScheduled(recipient, role,
        interviewStartTimeInUtc, secondParticipantRequest, interviewId);
  }

  /**
   * Sends interview rejection notifications.
   */
  public void sendInterviewRejectionNotifications(List<Interview> deletedInterviews) {
    try {
      long rejectorId = userContextProvider.getAuthenticatedUserId();
      long recipientId = deletedInterviews.stream()
          .map(Interview::getUserId)
          .filter(userId -> userId != rejectorId)
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(
              "Could not find other userId in the interview pair"));

      List<User> users = userService.findByIds(List.of(rejectorId, recipientId));
      Map<Long, User> userMap = users.stream()
          .collect(Collectors.toMap(User::getId, user -> user));

      notifyParticipantsAboutRejection(
          userMap.get(recipientId),
          userMap.get(rejectorId),
          deletedInterviews);
    } catch (Exception e) {
      log.error("Failed to send interview rejection notifications for interviews: {}",
          deletedInterviews, e);
    }
  }

  /**
   * Notifies users involved in the interview rejection.
   */
  private void notifyParticipantsAboutRejection(User recipient, User rejector,
      List<Interview> interviews) {
    Long recipientInterviewId = interviews.stream()
        .filter(i -> i.getUserId() == recipient.getId())
        .map(Interview::getId).findFirst().orElse(null);
    Long rejectorInterviewId = interviews.stream()
        .filter(i -> i.getUserId() == rejector.getId())
        .map(Interview::getId).findFirst().orElse(null);

    if (recipientInterviewId == null || rejectorInterviewId == null) {
      log.error("Could not find interview IDs for notification. Recipient: {}, Rejector: {}",
          recipient.getId(), rejector.getId());
      return;
    }

    ZonedDateTime scheduledTime = interviews.getFirst().getStartTime();
    notificationService.sendInterviewRejection(recipient, rejector, scheduledTime,
        recipientInterviewId);
    notificationService.sendInterviewRejection(rejector, recipient, scheduledTime,
        rejectorInterviewId);
  }
}