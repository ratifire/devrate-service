package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewFeedbackDto;
import com.ratifire.devrate.entity.interview.InterviewHistory;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing interview feedback.
 */
@Service
@RequiredArgsConstructor
public class InterviewFeedbackService {

  private final InterviewHistoryService interviewHistoryService;
  private final InterviewMetricsService interviewMetricsService;
  private final UserContextProvider userContextProvider;

  /**
   * Saves feedback for an interview.
   *
   * @param feedbackDto the DTO containing feedback details for the interview.
   */
  @Transactional
  public void saveFeedback(InterviewFeedbackDto feedbackDto) {
    long userId = userContextProvider.getAuthenticatedUserId();
    Optional<InterviewHistory> existingInterviewHistoryOpt =
        interviewHistoryService.findByInterviewIdAndUserId(feedbackDto.getInterviewId(), userId);

    if (existingInterviewHistoryOpt.isEmpty()) {
      interviewHistoryService.create(feedbackDto);
    } else {
      Map<InterviewRequestRole, InterviewHistory> interviewHistoriesMap =
          interviewHistoryService.updateExisting(feedbackDto, existingInterviewHistoryOpt.get());

      interviewMetricsService
          .updateAllMarksAndCountersAfterGettingFeedback(interviewHistoriesMap);
    }
  }
}