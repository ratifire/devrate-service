package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.Feedback;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.FeedbackRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing Feedbacks.
 */
@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;

  /**
   * Checks if a user can send feedback based on the latest feedback's date.
   *
   * @param user the user to check
   * @return true if the user can send feedback, false otherwise
   */
  public boolean isUserAbleToSendFeedback(User user) {
    return feedbackRepository.findLatestFeedbackByUser(user)
        .map(feedback -> feedback.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(1)))
        .orElse(true);
  }

  public void save(Feedback feedback) {
    feedbackRepository.save(feedback);
  }
}
