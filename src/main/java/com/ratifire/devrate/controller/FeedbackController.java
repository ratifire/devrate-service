package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.FeedbackDto;
import com.ratifire.devrate.exception.FeedbackSubmissionLimitException;
import com.ratifire.devrate.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to feedback information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/platform-feedbacks")
public class FeedbackController {

  private final FeedbackService feedbackService;

  /**
   * Adds feedback for a user if eligible, otherwise throws a FeedbackSubmissionLimitException.
   *
   * @param userId      the user's ID
   * @param feedbackDto the feedback details to add
   */
  @PreAuthorize("@resourceAuthorizationService.isPathUserIdMatchingLoggedUser(#userId)")
  @PostMapping
  public void addFeedback(@PathVariable long userId, @Valid @RequestBody FeedbackDto feedbackDto) {
    if (feedbackService.isUserAbleToSendFeedback(userId)) {
      feedbackService.addFeedback(userId, feedbackDto);
    } else {
      throw new FeedbackSubmissionLimitException(userId);
    }
  }
}
