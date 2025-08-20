package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewCreatedResultDto;
import com.ratifire.devrate.dto.InterviewRequestMailDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserService;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending notifications (WebSocket + Email), interview scheduling events.
 */
@Service
@RequiredArgsConstructor
public class InterviewNotificationService {

  private final NotificationService notificationService;
  private final EmailService emailService;
  private final UserService userService;

  /**
   * Sends scheduled interview notifications to both interviewer and candidate.
   */
  public void sendScheduledNotifications(InterviewCreatedResultDto result) {
    InterviewRequestMailDto candidateRequestMailDto = result.candidateMailDto();
    InterviewRequestMailDto interviewerRequestMailDto = result.interviewerMailDto();

    notifyParticipant(candidateRequestMailDto.userId(), candidateRequestMailDto.role(),
        interviewerRequestMailDto,
        result.date(),
        result.candidateInterviewId());

    notifyParticipant(interviewerRequestMailDto.userId(), interviewerRequestMailDto.role(),
        candidateRequestMailDto,
        result.date(),
        result.interviewerInterviewId());
  }

  private void notifyParticipant(long userId, InterviewRequestRole requestRole,
      InterviewRequestMailDto oppositeRequestMailDto,
      ZonedDateTime interviewStartTimeInUtc,
      long interviewId) {
    User recipient = userService.findById(userId);
    String recipientEmail = recipient.getEmail();
    String role = String.valueOf(requestRole);

    notificationService.addInterviewScheduled(recipient, role, interviewStartTimeInUtc,
        interviewId);
    emailService.sendInterviewScheduledEmail(recipient, recipientEmail, interviewStartTimeInUtc,
        oppositeRequestMailDto);
  }
}
