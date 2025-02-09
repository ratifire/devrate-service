package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewFeedbackDetailDto;
import com.ratifire.devrate.dto.InterviewFeedbackDto;
import com.ratifire.devrate.service.interview.InterviewFeedbackService;
import com.ratifire.devrate.service.interview.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to feedback detail information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/interview-feedbacks")
public class InterviewFeedbackController {

  private final InterviewFeedbackService interviewFeedbackService;
  private final InterviewService interviewService;

  /**
   * Retrieves the interview feedback detail for the specified ID.
   *
   * @param id the ID of the interview
   * @return the InterviewFeedbackDetailDto containing interview feedback information
   */
  @GetMapping("/{id}")
  public InterviewFeedbackDetailDto getFeedbackDetail(@PathVariable long id) {
    return interviewService.getFeedbackDetail(id);
  }

  /**
   * Handles requests to save interview feedback.
   *
   * @param interviewFeedbackDto the DTO containing feedback details to be saved
   */
  @PostMapping()
  public void saveFeedback(@Valid @RequestBody InterviewFeedbackDto interviewFeedbackDto) {
    interviewFeedbackService.saveFeedback(interviewFeedbackDto);
  }
}