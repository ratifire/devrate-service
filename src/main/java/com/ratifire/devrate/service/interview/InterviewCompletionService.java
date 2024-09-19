package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.service.specialization.SpecializationService;
import com.ratifire.devrate.service.user.UserService;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest.Payload.Meeting;
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

  private final InterviewService interviewService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewSummaryService interviewSummaryService;
  private final InterviewFeedbackDetailService interviewFeedbackDetailService;
  private final SpecializationService specializationService;
  private final UserService userService;
  private final ZoomApiService zoomApiService;

  /**
   * Completes the interview process by performing the necessary operations after a meeting has
   * ended.
   *
   * @param meeting The meeting object containing details of the meeting that has ended.
   * @return A success message indicating that the interview process was completed successfully.
   */
  @Transactional
  public String completeInterviewProcess(Meeting meeting) {
    logger.info("Zoom webhook triggered meeting.ended - {}", meeting.toString());
    Interview interview = interviewService.getInterviewByMeetingId(Long.parseLong(meeting.getId()));
    long interviewSummaryId = interviewSummaryService.createInterviewSummary(interview,
        meeting.getEndTime());
    Map<String, Long> interviewFeedbackDetailId =
        interviewFeedbackDetailService.saveInterviewFeedbackDetail(interview, interviewSummaryId);
    specializationService.updateUserInterviewCounts(interview);
    userService.refreshUserInterviewCounts(List.of(interview.getInterviewerRequest().getUser(),
        interview.getCandidateRequest().getUser()));
    try {
      zoomApiService.deleteMeeting(Long.parseLong(meeting.getId()));
    } catch (ZoomApiException e) {
      logger.error("Zoom API exception occurred while trying to delete the meeting "
          + "with meetingId: {}. {}", meeting.getId(), e.getMessage());
    }
    //TODO: add logic for sending notifications to users (candidate and interviewer)
    // about the interview feedback (need to transmit to front-end the next parameter:
    // interviewFeedbackDetailId)
    interviewService.deleteInterview(interview.getId());
    interviewRequestService.deleteInterviewRequests(
        List.of(interview.getInterviewerRequest().getId(),
            interview.getCandidateRequest().getId()));
    return "Interview process completed successfully!";
  }
}