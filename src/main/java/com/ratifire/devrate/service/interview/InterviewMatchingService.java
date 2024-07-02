package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.util.interview.InterviewPair;
import java.util.List;
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
   * @return an Optional containing the matched interview pair
   */
  @Transactional
  public Optional<InterviewPair<InterviewRequest, InterviewRequest>> match(
      InterviewRequest request) {
    return match(request, List.of());
  }

  /**
   * Forces matching for the given interview request with an ignore list.
   *
   * @param request    the interview request to match
   * @param ignoreList the list of users to be ignored in the matching process
   * @return an Optional containing the matched interview pair
   */
  @Transactional
  public Optional<InterviewPair<InterviewRequest, InterviewRequest>> match(
      InterviewRequest request, List<User> ignoreList) {
    return request.getRole() == INTERVIEWER ? getMatchedCandidate(request, ignoreList)
        .map(candidate -> InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(candidate)
            .interviewer(request)
            .build()) : getMatchedInterviewer(request, ignoreList)
        .map(interviewer -> InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(request)
            .interviewer(interviewer)
            .build());
  }

  /**
   * Marks the given matched pair as non-active.
   *
   * @param interviewPair the matched interview pair to mark as non-active
   */
  public void markPairAsNonActive(InterviewPair<InterviewRequest, InterviewRequest> interviewPair) {
    interviewRequestService.markAsNonActive(interviewPair.getCandidate());
    interviewRequestService.markAsNonActive(interviewPair.getInterviewer());
  }

  /**
   * Retrieves the first matched candidate based on the given interview request with an ignore
   * list.
   *
   * @param request the interview request specifying criteria for matching
   * @return an Optional containing the first matched InterviewRequest, if found
   */
  private Optional<InterviewRequest> getMatchedCandidate(InterviewRequest request,
      List<User> ignoreList) {
    return interviewRequestService.findMatchedCandidates(request, ignoreList).stream().findAny();
  }

  /**
   * Retrieves the first matched interviewer based on the given interview request with an ignore
   * list.
   *
   * @param request The interview request specifying criteria for matching.
   * @return An Optional containing the first matched InterviewRequest.
   */
  private Optional<InterviewRequest> getMatchedInterviewer(InterviewRequest request,
      List<User> ignoreList) {
    return interviewRequestService.findMatchedInterviewers(request, ignoreList).stream().findAny();
  }
}
