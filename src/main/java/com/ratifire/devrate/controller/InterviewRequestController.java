package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
   * Adds an interview request for the specified user.
   *
   * @param interviewRequest the interview request data
   */
  @PostMapping
  public void create(@Valid @RequestBody InterviewRequestDto interviewRequest,
      @AuthenticationPrincipal long userId) {
    interviewRequestService.create(interviewRequest, userId);
  }

  /**
   * Retrieves the interview request for a specific user and role.
   *
   * @param userId    the ID of the user
   * @param role      the role for which the interview request is being retrieved
   * @param masteryId the mastery id for which the interview request is being retrieved
   * @return the InterviewRequestDto containing the interview request details
   */
  @GetMapping
  public InterviewRequestDto find(@AuthenticationPrincipal long userId,
      @Valid @RequestParam InterviewRequestRole role, @Valid @RequestParam long masteryId) {
    return interviewRequestService.find(userId, role, masteryId);
  }

  /**
   * Updates the interview request for a specific user.
   *
   * @param userId           the ID of the user
   * @param interviewRequest the InterviewRequestDto containing the updated interview request
   */
  @PutMapping
  public void update(@AuthenticationPrincipal long userId,
      @Valid @RequestBody InterviewRequestDto interviewRequest) {
    interviewRequestService.update(userId, interviewRequest);
  }

  /**
   * Deletes an interview request for the specified request ID.
   *
   * @param requestId the ID of the interview request to be deleted
   */
  @DeleteMapping("/interview-requests/{requestId}")
  public void deleteInterviewRequest(@PathVariable long requestId) {
    interviewRequestService.delete(requestId);
  }
}
