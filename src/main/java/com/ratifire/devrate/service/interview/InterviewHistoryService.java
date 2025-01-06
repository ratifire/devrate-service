package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewHistoryNotFoundException;
import com.ratifire.devrate.exception.RoleNotFoundException;
import com.ratifire.devrate.repository.InterviewHistoryRepository;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.service.UserService;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewHistoryService {

  private final InterviewHistoryRepository interviewHistoryRepository;

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

  public void deleted(long userId, long id) {
  }
}