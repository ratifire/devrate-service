package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
public class InterviewRequestService {

  private final InterviewRequestRepository interviewRequestRepository;
  private final InterviewService interviewService;
  private static final Logger logger = LoggerFactory.getLogger(InterviewRequestService.class);

  /**
   * Forces matching for the given interview request.
   *
   * @param request the interview request to match
   */
  @Transactional
  public void forceMatching(InterviewRequest request) {
    if (request.getRole() == InterviewRequestRole.INTERVIEWER) {
      forceCandidateSearching(request);
    } else {
      forceInterviewerSearching(request);
    }
  }

  /**
   * Forces searching for a candidate when the request is from an interviewer.
   *
   * @param interviewerRequest the interview request from an interviewer
   */
  private void forceCandidateSearching(InterviewRequest interviewerRequest) {
    getMatchedCandidate(interviewerRequest)
        .ifPresentOrElse(candidateRequest -> {
          interviewService.createInterview(candidateRequest, interviewerRequest);
          markAsNonActive(candidateRequest, interviewerRequest);
        }, () -> logger.debug("No matching candidate found for interviewer request: {}",
            interviewerRequest));
  }

  /**
   * Forces searching for an interviewer when the request is from a candidate.
   *
   * @param candidateRequest the interview request from a candidate
   */
  private void forceInterviewerSearching(InterviewRequest candidateRequest) {
    getMatchedInterviewer(candidateRequest)
        .ifPresentOrElse(interviewerRequest -> {
          interviewService.createInterview(candidateRequest, interviewerRequest);
          markAsNonActive(candidateRequest, interviewerRequest);
        }, () -> logger.debug("No matching interviewer found for candidate request: {}",
            candidateRequest));
  }

  /**
   * Marks a candidate and an interviewer request as non-active.
   *
   * @param candidateRequest   the candidate request to mark as non-active
   * @param interviewerRequest the interviewer request to mark as non-active
   */
  private void markAsNonActive(InterviewRequest candidateRequest,
      InterviewRequest interviewerRequest) {

    candidateRequest.setActive(false);
    interviewRequestRepository.save(candidateRequest);

    interviewerRequest.setActive(false);
    interviewRequestRepository.save(interviewerRequest);
  }

  /**
   * Retrieves the first matched candidate based on the given interview request.
   *
   * @param request The interview request specifying criteria for matching.
   * @return An Optional containing the first matched InterviewRequest.
   */
  private Optional<InterviewRequest> getMatchedCandidate(InterviewRequest request) {
    List<InterviewRequest> matchedCandidates = interviewRequestRepository.findMatchedCandidates(
        request);
    return matchedCandidates.isEmpty()
        ? Optional.empty()
        : Optional.of(matchedCandidates.getFirst());
  }

  /**
   * Retrieves the first matched interviewer based on the given interview request.
   *
   * @param request The interview request specifying criteria for matching.
   * @return An Optional containing the first matched InterviewRequest.
   */
  private Optional<InterviewRequest> getMatchedInterviewer(InterviewRequest request) {
    List<InterviewRequest> matchedInterviewers = interviewRequestRepository.findMatchedInterviewers(
        request);
    return matchedInterviewers.isEmpty()
        ? Optional.empty()
        : Optional.of(matchedInterviewers.getFirst());
  }

  /**
   * Handles the actions required after an interview is rejected.
   *
   * @param activeRequest The active interview request.
   * @param rejectedRequest The rejected interview request.
   */
  public void handleRejectedInterview(InterviewRequest activeRequest,
      InterviewRequest rejectedRequest) {
    activeRequest.setActive(true);
    rejectedRequest.setActive(true);
    interviewRequestRepository.save(activeRequest);
    interviewRequestRepository.save(rejectedRequest);

    List<Long> userIdBlackList = List.of(rejectedRequest.getUser().getId());
  }
}
