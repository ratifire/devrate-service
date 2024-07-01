package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.util.interview.InterviewPair;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for matching interview requests between candidates and interviewers.
 */
@Service
@RequiredArgsConstructor
public class InterviewMatchingService {

  private final InterviewRequestService interviewRequestService;

  /**
   * Forces matching for the given interview request.
   *
   * @param request the interview request to match
   */
  @Transactional
  public Optional<InterviewRequest> match(InterviewRequest request) {
    return request.getRole() == INTERVIEWER
        ? getMatchedCandidate(request)
        : getMatchedInterviewer(request);
  }

  /**
   * Marks the given matched pair as non-active.
   *
   * @param interviewPair the matched interview pair to mark as non-active
   */
  public void markPairAsNonActive(InterviewPair interviewPair) {
    interviewRequestService.markAsNonActive(interviewPair.getCandidate());
    interviewRequestService.markAsNonActive(interviewPair.getInterviewer());
  }

  /**
   * Retrieves the first matched candidate based on the given interview request.
   *
   * @param request the interview request specifying criteria for matching
   * @return an Optional containing the first matched InterviewRequest, if found
   */
  private Optional<InterviewRequest> getMatchedCandidate(InterviewRequest request) {
    return interviewRequestService.findMatchedCandidates(request).stream().findAny();
  }

  /**
   * Retrieves the first matched interviewer based on the given interview request.
   *
   * @param request The interview request specifying criteria for matching.
   * @return An Optional containing the first matched InterviewRequest.
   */
  private Optional<InterviewRequest> getMatchedInterviewer(InterviewRequest request) {
    return interviewRequestService.findMatchedInterviewers(request).stream().findAny();
  }
}
