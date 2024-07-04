package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
public class InterviewRequestService {

  private final InterviewRequestRepository interviewRequestRepository;

  /**
   * Saves the given interview request.
   *
   * @param interviewRequest the interview request to be saved
   * @return the saved interview request
   */
  public InterviewRequest save(InterviewRequest interviewRequest) {
    return interviewRequestRepository.save(interviewRequest);
  }

  /**
   * Finds matched candidates for the given interview request with an ignore list.
   *
   * @param request the interview request specifying criteria for matching
   * @return a list of matched InterviewRequest entities
   */
  public List<InterviewRequest> findMatchedCandidates(InterviewRequest request,
      List<User> ignoreList) {
    return interviewRequestRepository.findMatchedCandidates(request, ignoreList);
  }

  /**
   * Finds matched interviewers for the given interview request with an ignore list.
   *
   * @param request the interview request specifying criteria for matching
   * @return a list of matched InterviewRequest entities
   */
  public List<InterviewRequest> findMatchedInterviewers(InterviewRequest request,
      List<User> ignoreList) {
    return interviewRequestRepository.findMatchedInterviewers(request, ignoreList);
  }

  /**
   * Marks the given interview request as non-active.
   *
   * @param request the interview request to mark as non-active
   */
  public void markAsNonActive(InterviewRequest request) {
    request.setActive(false);
    interviewRequestRepository.save(request);
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
  }
}
