package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ClosestEventDto;
import com.ratifire.devrate.dto.InterviewDto;
import com.ratifire.devrate.dto.InterviewEventDto;
import com.ratifire.devrate.dto.InterviewsOverallStatusDto;
import com.ratifire.devrate.service.interview.InterviewService;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public Page<InterviewDto> findAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    return interviewService.findAll(page, size);
  }

  /**
   * Retrieves a single visible interview by id for the user.
   *
   * @return an InterviewDto object by id
   */
  @GetMapping("/{id}/visible")
  public InterviewDto findVisibleInterviewById(@PathVariable long id) {
    return interviewService.findVisibleInterviewById(id);
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

  /**
   * Retrieves a list of events for a given user that start from a specified date and time.
   *
   * @param from   the starting date and time in ISO format (e.g., {@code 2024-08-28T12:00:00Z})
   * @return a list of {@link ClosestEventDto} objects representing the events starting from
   */
  @GetMapping("/events/closest")
  public List<ClosestEventDto> findUpcomingEvents(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from) {
    return interviewService.findUpcomingEvents(from);
  }

  /**
   * Retrieves a status for overall interviews.
   *
   * @param userTimeZone the user timezone
   * @return status
   */
  @GetMapping("/status-indicator")
  public InterviewsOverallStatusDto getInterviewStatusIndicator(@RequestParam String userTimeZone) {
    return interviewService.getInterviewStatusIndicator(userTimeZone);
  }

  /**
   * Retrieve the interview meeting room link.
   *
   * @param id the ID of the interview
   * @return A valid meeting room URL.
   */
  @GetMapping("/{id}/meeting/join")
  public String redirectToInterviewRoom(@PathVariable long id) {
    return interviewService.getOrCreateInterviewRoom(id);
  }
}