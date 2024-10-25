package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.FeedbackDto;
import com.ratifire.devrate.entity.Feedback;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.FeedbackRepository;
import com.ratifire.devrate.service.user.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing Feedbacks.
 */
@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final UserService userService;
  private final FeedbackRepository feedbackRepository;
  private final DataMapper<FeedbackDto, Feedback> feedbackMapper;

  /**
   * Checks if a user can send feedback based on the latest feedback's date.
   *
   * @param userId the ID of the user to check
   * @return true if the user can send feedback, false otherwise
   */
  public boolean isUserAbleToSendFeedback(long userId) {
    return feedbackRepository.existsFeedbackWithinLastMonth(userId,
        LocalDateTime.now().minusMonths(1));
  }

  /**
   * Adds feedback for a user if they are eligible, otherwise throws exception.
   *
   * @param userId      the ID of the user
   * @param feedbackDto the feedback details to add
   */
  public void addFeedback(long userId, FeedbackDto feedbackDto) {
    User user = userService.findUserById(userId);
    Feedback entity = feedbackMapper.toEntity(feedbackDto);
    entity.setUser(user);
    feedbackRepository.save(entity);
  }
}
