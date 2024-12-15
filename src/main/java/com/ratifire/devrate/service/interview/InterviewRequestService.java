package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.exception.InterviewRequestNotFoundException;
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

  private final InterviewRequestRepository repository;

  /**
   * Saves the given interview request.
   *
   * @param interviewRequest the interview request to be saved
   * @return the saved interview request
   */
  public InterviewRequest save(InterviewRequest interviewRequest) {
    return repository.save(interviewRequest);
  }

  /**
   * Finds an interview request for the given userId, role and mastery. Throw error if not found.
   *
   * @param userId    the ID of the user associated with the interview request
   * @param role      the role of the interview request
   * @param masteryId the mastery id of the interview request
   * @return the found InterviewRequest or an empty InterviewRequest if not found
   */
  public InterviewRequest findByUserIdRoleMasteryId(long userId, InterviewRequestRole role,
      long masteryId) {
    return repository.findByUserIdAndRoleAndMastery_Id(userId, role, masteryId)
        .orElseThrow(
            () -> new InterviewRequestDoesntExistException(userId, role.name(), masteryId));
  }

  /**
   * Finds matched candidates for the given interview request with an ignore list.
   *
   * @param request the interview request specifying criteria for matching
   * @return a list of matched InterviewRequest entities
   */
  public List<InterviewRequest> findMatchedCandidates(InterviewRequest request,
      List<User> ignoreList) {
    return repository.findMatchedCandidates(request, ignoreList);
  }

  /**
   * Finds matched interviewers for the given interview request with an ignore list.
   *
   * @param request the interview request specifying criteria for matching
   * @return a list of matched InterviewRequest entities
   */
  public List<InterviewRequest> findMatchedInterviewers(InterviewRequest request,
      List<User> ignoreList) {
    return repository.findMatchedInterviewers(request, ignoreList);
  }

  /**
   * Marks the given interview request as non-active.
   *
   * @param request the interview request to mark as non-active
   */
  public void markAsNonActive(InterviewRequest request) {
    request.setActive(false);
    repository.save(request);
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
    repository.save(activeRequest);
    repository.save(rejectedRequest);
  }

  /**
   * Deletes interview requests by their IDs.
   *
   * @param ids the list of interview request IDs to be deleted.
   */
  public void deleteBulk(List<Long> ids) {
    repository.deleteAllById(ids);
  }

  /**
   * Deletes an interview request by its ID if the request is active.
   *
   * @param id the ID of the interview request to be deleted
   */
  public void delete(long id) {
    InterviewRequest interviewRequest = findById(id);
    if (interviewRequest.isActive()) {
      repository.delete(interviewRequest);
    }
  }

  /**
   * Finds an interview request by its ID. Throws an exception if not found.
   *
   * @param id the ID of the interview request
   * @return the found InterviewRequest
   * @throws InterviewRequestNotFoundException if no interview request is found with the given ID
   */
  private InterviewRequest findById(long id) {
    return repository.findById(id)
        .orElseThrow(() -> new InterviewRequestNotFoundException(id));
  }
}
