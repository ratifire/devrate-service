package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.util.InterviewPair;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for matching interview requests between candidates and interviewers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewMatchingService {

  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;

  /**
   * Matches the given interview request with an existing request.
   *
   * @param incomingRequest the interview request to be matched
   */
  public Optional<Interview> match(InterviewRequest incomingRequest) {
    return match(incomingRequest, List.of());
  }

  /**
   * Matches the given interview request with an existing request, considering an ignore list.
   *
   * @param incomingRequest the interview request to be matched
   * @param ignoreList      a list of users to ignore during matching
   * @return an Optional containing the created Interview
   */
  public Optional<Interview> match(InterviewRequest incomingRequest, List<User> ignoreList) {
    return getInterviewPair(incomingRequest, ignoreList)
        .flatMap(interviewPair -> {
          Optional<Interview> interviewOptional = interviewService.create(interviewPair);
          interviewOptional.ifPresent(interview -> markPairAsNonActive(interviewPair));
          return interviewOptional;
        })
        .or(() -> {
          log.debug("No matching request found for: {}", incomingRequest);
          return Optional.empty();
        });
  }

  /**
   * Marks the given matched pair as non-active.
   *
   * @param interviewPair the matched interview pair to mark as non-active
   */
  public void markPairAsNonActive(InterviewPair interviewPair) {
    interviewRequestService.markAsNonActive(interviewPair.candidate());
    interviewRequestService.markAsNonActive(interviewPair.interviewer());
  }

  /**
   * Retrieves a matched pair of interview requests.
   *
   * @param incomingRequest the interview request to be matched
   * @param ignoreList      a list of users to ignore during matching
   * @return an optional InterviewPair containing the matched candidate and interviewer
   */
  private Optional<InterviewPair> getInterviewPair(
      InterviewRequest incomingRequest, List<User> ignoreList) {
    return incomingRequest.getRole() == INTERVIEWER
        ? getMatchedCandidate(incomingRequest, ignoreList)
            .map(candidate -> new InterviewPair(candidate, incomingRequest))
        : getMatchedInterviewer(incomingRequest, ignoreList)
            .map(interviewer -> new InterviewPair(incomingRequest, interviewer));
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
