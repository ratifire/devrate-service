package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewFeedbackDto;
import com.ratifire.devrate.entity.interview.InterviewHistory;
import com.ratifire.devrate.security.helper.UserContextProvider;
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
  private final InterviewCompletionService interviewCompletionService;
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
      interviewHistoryService.updateExisting(feedbackDto,
          existingInterviewHistoryOpt.get());
      //TODO: Triger interview completion process

      //      interviewCompletionService.finalizeInterviewProcess();
    }
  }
}