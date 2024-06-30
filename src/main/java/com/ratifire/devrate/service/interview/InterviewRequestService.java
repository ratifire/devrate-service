package com.ratifire.devrate.service.interview;

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
   * Finds matched candidates for the given interview request.
   *
   * @param request the interview request specifying criteria for matching
   * @return a list of matched InterviewRequest entities
   */
  public List<InterviewRequest> findMatchedCandidates(InterviewRequest request) {
    return interviewRequestRepository.findMatchedCandidates(request);
  }

  /**
   * Finds matched interviewers for the given interview request.
   *
   * @param request the interview request specifying criteria for matching
   * @return a list of matched InterviewRequest entities
   */
  public List<InterviewRequest> findMatchedInterviewers(InterviewRequest request) {
    return interviewRequestRepository.findMatchedInterviewers(request);
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
}
