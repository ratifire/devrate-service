package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewRequestViewDto;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling interview requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/interview-requests")
public class InterviewRequestController {

  private final InterviewRequestService interviewRequestService;

  /**
   * Retrieves all interview requests for the authenticated user.
   *
   * @return a list of {@link InterviewRequestViewDto} containing the details.
   */
  @GetMapping
  public List<InterviewRequestViewDto> getAll() {
    return interviewRequestService.getAll();
  }

  /**
   * Retrieves a list of interview requests associated with a specific mastery ID.
   *
   * @param masteryId the ID of the mastery to fetch interview requests for.
   * @return a list of {@link InterviewRequestViewDto} containing the details.
   */
  @GetMapping("/masteries/{masteryId}")
  public List<InterviewRequestViewDto> getByMasteryId(@PathVariable long masteryId) {
    return interviewRequestService.getByMasteryId(masteryId);
  }

  /**
   * Creates a new interview request and sends it to the matcher-service.
   *
   * @param interviewRequest the interview request data
   */
  @PostMapping
  public void create(@Valid @RequestBody InterviewRequestDto interviewRequest) {
    interviewRequestService.create(interviewRequest);
  }

  /**
   * Updates the interview request for a specific user.
   *
   * @param id               the interview request ID
   * @param interviewRequest the updated interview request data
   */
  @PutMapping("/{id}")
  public void update(@PathVariable long id,
      @Valid @RequestBody InterviewRequestDto interviewRequest) {
    interviewRequestService.update(id, interviewRequest);
  }

  /**
   * Deletes an interview request for the specified request ID.
   *
   * @param id the ID of the interview request to be deleted
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    interviewRequestService.delete(id);
  }
}
