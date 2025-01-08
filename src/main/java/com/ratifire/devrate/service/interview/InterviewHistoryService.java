package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewHistoryDto;
import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.exception.InterviewHistoryNotFoundException;
import com.ratifire.devrate.mapper.impl.InterviewHistoryMapper;
import com.ratifire.devrate.repository.InterviewHistoryRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewHistoryService {

  private final InterviewHistoryRepository interviewHistoryRepository;
  private final UserContextProvider userContextProvider;
  private final InterviewHistoryMapper interviewHistoryMapper;

  /**
   * Retrieves an InterviewSummary entity by its identifier.
   *
   * @param id the unique identifier of the InterviewSummary to be retrieved.
   * @return the InterviewSummary associated with the provided id.
   * @throws InterviewHistoryNotFoundException if no InterviewSummary is found for the given id.
   */
  public InterviewHistory findByIdAndUserId(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();
    return interviewHistoryRepository.findByIdAndUserId(id, userId);
  }

  /**
   * Retrieves all InterviewHistory entities associated with the authenticated user.
   *
   * @return a list of InterviewHistory entities linked to the current user.
   */
  public Page<InterviewHistoryDto> getAllByUserId(int page, int size) {
    long userId = userContextProvider.getAuthenticatedUserId();
    Pageable pageable = PageRequest.of(page, size);
    return interviewHistoryRepository.findAllByUserId(userId, pageable)
        .map(interviewHistoryMapper::toDto);
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