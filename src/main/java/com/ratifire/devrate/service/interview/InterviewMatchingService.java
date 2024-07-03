package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.util.interview.InterviewPair;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for matching interview requests between candidates and interviewers.
 */
@Service
@RequiredArgsConstructor
public class InterviewMatchingService {

  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;
  private static final Logger logger = LoggerFactory.getLogger(InterviewMatchingService.class);

  /**
   * Matches the given interview request with an existing request.
   *
   * @param incomingRequest the interview request to be matched
   */
  public void match(InterviewRequest incomingRequest) {
    match(incomingRequest, List.of());
  }

  /**
   * Matches the given interview request with an existing request, considering an ignore list.
   *
   * @param incomingRequest the interview request to be matched
   * @param ignoreList      a list of users to ignore during matching
   */
  public void match(InterviewRequest incomingRequest, List<User> ignoreList) {
    getInterviewPair(incomingRequest, ignoreList)
        .ifPresentOrElse(interviewPair -> {
          interviewService.createInterview(interviewPair);
          markPairAsNonActive(interviewPair);
        }, () -> logger.debug("No matching request found for: {}", incomingRequest));
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
   * Retrieves a matched pair of interview requests.
   *
   * @param incomingRequest the interview request to be matched
   * @param ignoreList      a list of users to ignore during matching
   * @return an optional InterviewPair containing the matched candidate and interviewer
   */
  private Optional<InterviewPair<InterviewRequest, InterviewRequest>> getInterviewPair(
      InterviewRequest incomingRequest, List<User> ignoreList) {
    return incomingRequest.getRole() == INTERVIEWER ? getMatchedCandidate(incomingRequest,
        ignoreList)
        .map(candidate -> InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(candidate)
            .interviewer(incomingRequest)
            .build()) : getMatchedInterviewer(incomingRequest, ignoreList)
        .map(interviewer -> InterviewPair.<InterviewRequest, InterviewRequest>builder()
            .candidate(incomingRequest)
            .interviewer(interviewer)
            .build());
  }

  /**
   * Finds a matched candidate for the given interview request.
   *
   * @param request    the interview request specifying matching criteria
   * @param ignoreList a list of users to ignore during matching
   * @return an Optional containing the matched InterviewRequest, if found
   */
  private Optional<InterviewRequest> getMatchedCandidate(InterviewRequest request,
      List<User> ignoreList) {
    return interviewRequestService.findMatchedCandidates(request, ignoreList).stream().findAny();
  }

  /**
   * Finds a matched interviewer for the given interview request.
   *
   * @param request    the interview request specifying matching criteria
   * @param ignoreList a list of users to ignore during matching
   * @return an Optional containing the matched InterviewRequest, if found
   */
  private Optional<InterviewRequest> getMatchedInterviewer(InterviewRequest request,
      List<User> ignoreList) {
    return interviewRequestService.findMatchedInterviewers(request, ignoreList).stream().findAny();
  }
}
