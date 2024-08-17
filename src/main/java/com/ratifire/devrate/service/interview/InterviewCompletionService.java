package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.service.specialization.SpecializationService;
import com.ratifire.devrate.service.user.UserService;
import com.ratifire.devrate.util.zoom.webhook.model.WebHookRequest.Payload.Meeting;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for handling the completion process of an interview.
 */
@Service
@RequiredArgsConstructor
public class InterviewCompletionService {

  private final InterviewService interviewService;
  private final InterviewSummaryService interviewSummaryService;
  private final SpecializationService specializationService;
  private final UserService userService;

  /**
   * Completes the interview process by performing the necessary operations after a meeting has
   * ended.
   *
   * @param meeting The meeting object containing details of the meeting that has ended.
   * @return A success message indicating that the interview process was completed successfully.
   */
  @Transactional
  public String completeInterviewProcess(Meeting meeting) {
    Interview interview = interviewService.getInterviewByMeetingId(Long.parseLong(meeting.getId()));
    interviewSummaryService.saveInterviewSummary(interview, meeting.getEndTime());
    specializationService.updateUserInterviewCounts(interview);
    userService.refreshUserInterviewCounts(List.of(interview.getInterviewerRequest().getUser(),
        interview.getCandidateRequest().getUser()));
    //TODO: add the other needed logic for completing the interview process
    return "Interview process completed successfully!";
  }
}