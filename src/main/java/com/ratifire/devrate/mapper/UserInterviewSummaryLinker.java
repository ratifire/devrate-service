package com.ratifire.devrate.mapper;

import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component responsible for linking interview summaries with user entities and persisting the
 * changes.
 */
@Component
@RequiredArgsConstructor
public class UserInterviewSummaryLinker {

  private final UserRepository userRepository;

  public void linkAndSaveInterviewSummaryToUsers(List<User> users,
      InterviewSummary interviewSummary) {
    users.forEach(user -> user.getInterviewSummaries().add(interviewSummary));
    userRepository.saveAll(users);
  }

}