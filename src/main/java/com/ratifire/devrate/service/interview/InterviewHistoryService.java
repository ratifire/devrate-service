package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewHistoryDto;
import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.InterviewHistoryNotFoundException;
import com.ratifire.devrate.mapper.impl.InterviewHistoryMapper;
import com.ratifire.devrate.repository.InterviewHistoryRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewHistoryService {

  private final InterviewHistoryRepository interviewHistoryRepository;
  private final UserContextProvider userContextProvider;
  private final InterviewHistoryMapper interviewHistoryMapper;
  private final UserRepository userRepository;

  /**
   * Retrieves an InterviewSummary entity by its identifier.
   *
   * @param id the unique identifier of the InterviewSummary to be retrieved.
   * @return the InterviewSummary associated with the provided id.
   * @throws InterviewHistoryNotFoundException if no InterviewSummary is found for the given id.
   */
  public InterviewHistoryDto findByIdAndUserId(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewHistory interviewHistory = interviewHistoryRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new InterviewHistoryNotFoundException(id));
    return interviewHistoryMapper.toDto(interviewHistory);
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
  @Transactional
  public void delete(long id) {
    InterviewHistory interviewHistory = interviewHistoryRepository.findById(id)
        .orElseThrow(() -> new InterviewHistoryNotFoundException(id));

    List<User> users = userRepository.findAllByInterviewHistoriesContaining(interviewHistory);
    users.forEach(user -> user.getInterviewHistories().remove(interviewHistory));

    userRepository.saveAll(users);
    interviewHistoryRepository.delete(interviewHistory);
  }
}