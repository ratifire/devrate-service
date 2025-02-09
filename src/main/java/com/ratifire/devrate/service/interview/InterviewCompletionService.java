package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.service.MeetingService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.SpecializationService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.webhook.exception.ZoomWebhookException;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest.Payload.Meeting;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling the completion process of an interview.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewCompletionService {

  private static final int WEBHOOK_ACTIVATION_DELAY = 10;

  private final InterviewService interviewService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewHistoryService interviewHistoryService;
  private final SpecializationService specializationService;
  private final UserService userService;
  private final NotificationService notificationService;
  private final MeetingService meetingService;

  //TODO: Methods needs to be reimplemented regarding to using another meeting provider
  @Transactional
  public void finalizeInterviewProcess(Meeting meeting)
      throws ZoomWebhookException, ZoomApiException {
    //TODO: Force recalculation marks process (skills, quantity of interviews etc.)

  //    if (!validateMeetingEndTime(meeting)) {
  //      log.info("Webhook ignored as it was triggered too early.");
  //      return;
  //    }
  //
  //    String meetingId = meeting.getId();
  //    log.info("Zoom webhook triggered meeting.ended - {}", meeting);
  //    Interview interview = interviewService.getByMeetingId(Long.parseLong(meetingId));
  //
  //    long interviewSummaryId = interviewSummaryService.create(interview,
  //        meeting.getEndTime());
  //
  //    specializationService.updateUserInterviewCounts(interview);
  //
  //    User interviewer = interview.getInterviewerRequest().getUser();
  //    User candidate = interview.getCandidateRequest().getUser();
  //    userService.recalculateInterviewCounts(List.of(interviewer, candidate));
  //
  //    Map<InterviewRequestRole, Long> feedbackDetailIdByRole =
  //        interviewFeedbackDetailService.save(interview, interviewSummaryId);
  //
  //    Long candidateFeedbackDetailId = feedbackDetailIdByRole.get(InterviewRequestRole.CANDIDATE);
  //    notificationService.addInterviewFeedbackDetail(candidate, candidateFeedbackDetailId,
  //        candidate.getEmail());
  //    Long interviewerFeedbackDetailId = feedbackDetailIdByRole.get(
  //    InterviewRequestRole.INTERVIEWER);
  //    notificationService.addInterviewFeedbackDetail(interviewer, interviewerFeedbackDetailId,
  //        interviewer.getEmail());
  //
  //    zoomApiService.deleteMeeting(Long.parseLong(meetingId));
  //
  //    interviewService.delete(interview.getId());
  //    interviewRequestService.deleteBulk(
  //        List.of(interview.getInterviewerRequest().getId(),
  //            interview.getCandidateRequest().getId()));
  //
  //    log.info("Interview with meeting id {} completed successfully!", meetingId);
  //  }

  //  /**
  //   * Validates if the webhook is triggered at the right time.
  //   *
  //   * @param meeting The meeting object containing details of the meeting that has ended.
  //   * @return boolean indicating if the webhook can proceed with interview completion.
  //   * @throws ZoomWebhookException If the data is invalid.
  //   */
  //  public boolean validateMeetingEndTime(Meeting meeting) throws ZoomWebhookException {
  //    if (meeting == null || meeting.getId() == null) {
  //      throw new ZoomWebhookException("Invalid meeting data");
  //    }
  //
  //    long meetingId = Long.parseLong(meeting.getId());
  //
  //    Interview interview = interviewService.getByMeetingId(meetingId);
  //    ZonedDateTime scheduledStartTime = interview.getStartTime();
  //    ZonedDateTime currentDateTime = DateTimeUtils.toUtc(ZonedDateTime.now());
  //
  //    // TODO: nee change to for prod
  //    //  if (currentDateTime.isBefore(
    //    scheduledStartTime.plusMinutes(WEBHOOK_ACTIVATION_DELAY))) {
  //    if (currentDateTime.isBefore(scheduledStartTime.plusSeconds(WEBHOOK_ACTIVATION_DELAY))) {
  //      return false;
  //    }
  //    return true;
  }
}