package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewDto;
import com.ratifire.devrate.dto.InterviewEventDto;
import com.ratifire.devrate.service.interview.InterviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles HTTP requests related to interview management.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/interviews")
public class InterviewController {

  private final InterviewService interviewService;

  /**
   * Retrieves a list of all interviews for the user.
   *
   * @return a list of InterviewDto objects
   */
  @GetMapping()
  public List<InterviewDto> getAll() {
    return interviewService.getAll();
  }

  /**
   * Deletes a rejected interview by its ID.
   *
   * @param id the ID of the interview to be deleted
   */
  @DeleteMapping("/{id}")
  public void deleteRejected(@PathVariable long id) {
    interviewService.deleteRejected(id);
  }

  /**
   * Retrieves the details of a specific interview event by its ID.
   *
   * @param id the ID of the event
   * @return an InterviewEventDto containing the event details
   */
  @GetMapping("/events/{id}")
  public InterviewEventDto getInterviewEventDetails(@PathVariable long id) {
    return interviewService.getInterviewEventDetails(id);
  }
}