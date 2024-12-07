package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.specialization.SpecializationService;
import com.ratifire.devrate.service.user.UserService;
import com.ratifire.devrate.util.DateTimeUtils;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest.Payload.Meeting;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling the completion process of an interview.
 */
@Service
@RequiredArgsConstructor
public class InterviewCompletionService {

  private static final Logger logger = LoggerFactory.getLogger(InterviewCompletionService.class);
  private static final int WEBHOOK_ACTIVATION_DELAY = 10;

  private final InterviewService interviewService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewSummaryService interviewSummaryService;
  private final InterviewFeedbackDetailService interviewFeedbackDetailService;
  private final SpecializationService specializationService;
  private final UserService userService;
  private final NotificationService notificationService;
  private final ZoomApiService zoomApiService;

  /**
   * Finalize the interview process by performing the necessary operations after a meeting has
   * ended.
   *
   * @param meeting The meeting object containing details of the meeting that has ended.
   */
  @Transactional
  public void finalizeInterviewProcess(Meeting meeting)
      throws ZoomWebhookException, ZoomApiException {
    if (!validateMeetingEndTime(meeting)) {
      logger.info("Webhook ignored as it was triggered too early.");
      return;
    }

    String meetingId = meeting.getId();
    logger.info("Zoom webhook triggered meeting.ended - {}", meeting);
    Interview interview = interviewService.getInterviewByMeetingId(Long.parseLong(meetingId));

    long interviewSummaryId = interviewSummaryService.createInterviewSummary(interview,
        meeting.getEndTime());

    specializationService.updateUserInterviewCounts(interview);

    User interviewer = interview.getInterviewerRequest().getUser();
    User candidate = interview.getCandidateRequest().getUser();
    userService.refreshUserInterviewCounts(List.of(interviewer, candidate));

    Map<InterviewRequestRole, Long> feedbackDetailIdByRole =
        interviewFeedbackDetailService.saveInterviewFeedbackDetail(interview, interviewSummaryId);

    Long candidateFeedbackDetailId = feedbackDetailIdByRole.get(InterviewRequestRole.CANDIDATE);
    notificationService.addInterviewFeedbackDetail(candidate, candidateFeedbackDetailId,
        candidate.getEmail());
    Long interviewerFeedbackDetailId = feedbackDetailIdByRole.get(InterviewRequestRole.INTERVIEWER);
    notificationService.addInterviewFeedbackDetail(interviewer, interviewerFeedbackDetailId,
        interviewer.getEmail());

    zoomApiService.deleteMeeting(Long.parseLong(meetingId));

    interviewService.deleteInterview(interview.getId());
    interviewRequestService.deleteInterviewRequests(
        List.of(interview.getInterviewerRequest().getId(),
            interview.getCandidateRequest().getId()));

    logger.info("Interview with meeting id {} completed successfully!", meetingId);
  }

  /**
   * Validates if the webhook is triggered at the right time.
   *
   * @param meeting The meeting object containing details of the meeting that has ended.
   * @return boolean indicating if the webhook can proceed with interview completion.
   * @throws ZoomWebhookException If the data is invalid.
   */
  public boolean validateMeetingEndTime(Meeting meeting) throws ZoomWebhookException {
    if (meeting == null || meeting.getId() == null) {
      throw new ZoomWebhookException("Invalid meeting data");
    }

    long meetingId = Long.parseLong(meeting.getId());

    Interview interview = interviewService.getInterviewByMeetingId(meetingId);
    ZonedDateTime scheduledStartTime = interview.getStartTime();
    ZonedDateTime currentDateTime = DateTimeUtils.toUtc(ZonedDateTime.now());

    // TODO: nee change to for prod
    //  if (currentDateTime.isBefore(scheduledStartTime.plusMinutes(WEBHOOK_ACTIVATION_DELAY))) {
    if (currentDateTime.isBefore(scheduledStartTime.plusSeconds(WEBHOOK_ACTIVATION_DELAY))) {
      return false;
    }
    return true;
  }
}