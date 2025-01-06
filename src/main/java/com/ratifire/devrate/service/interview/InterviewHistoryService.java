package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.exception.InterviewHistoryNotFoundException;
import com.ratifire.devrate.repository.InterviewHistoryRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewHistoryService {

  private final InterviewHistoryRepository interviewHistoryRepository;
  private final UserContextProvider userContextProvider;

  /**
   * Retrieves an InterviewSummary entity by its identifier.
   *
   * @param id the unique identifier of the InterviewSummary to be retrieved.
   * @return the InterviewSummary associated with the provided id.
   * @throws InterviewHistoryNotFoundException if no InterviewSummary is found for the given id.
   */
  public InterviewHistory findById(long id) {
    return interviewHistoryRepository.findById(id).orElseThrow(() ->
        new InterviewHistoryNotFoundException(id));
  }

  /**
   * Retrieves all InterviewHistory entities associated with the authenticated user.
   *
   * @return a list of InterviewHistory entities linked to the current user.
   */
  public List<InterviewHistory> getAllByUserId() {
    return interviewHistoryRepository.getAllByUserId(userContextProvider.getAuthenticatedUserId());
  }

  /**
   * Deletes an InterviewHistory entity by its identifier.
   *
   * @param id the unique identifier of the InterviewHistory to be deleted.
   * @throws InterviewHistoryNotFoundException if no InterviewHistory is found for the given id.
   */
  public void delete(long id) {
    if (!interviewHistoryRepository.existsById(id)) {
      throw new InterviewHistoryNotFoundException(id);
    }
    interviewHistoryRepository.deleteById(id);
  }
}