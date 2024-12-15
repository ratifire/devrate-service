package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewFeedbackDetailDto;
import com.ratifire.devrate.service.interview.InterviewFeedbackDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to feedback detail information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback-details")
public class InterviewFeedbackDetailController {

  private final InterviewFeedbackDetailService interviewFeedbackDetailService;


  /**
   * Retrieves the interview feedback detail for the specified ID.
   *
   * @param id the ID of the interview feedback detail to retrieve
   * @return the InterviewFeedbackDetailDto containing detailed feedback information
   */
  @GetMapping("/{id}")
  public InterviewFeedbackDetailDto getInterviewFeedbackDetail(@PathVariable long id) {
    return interviewFeedbackDetailService.getDtoById(id);
  }
}