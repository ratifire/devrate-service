package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewRequestViewDto;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
   * Returns the interview ID associated with the given time slot ID.
   *
   * @param id the ID of the time slot
   * @return the ID of the associated interview, or {@code 204 status} if no interview is linked
   */
  @GetMapping("/timeslots/{id}/interview-id")
  public ResponseEntity<Long> getInterviewIdByTimeSlot(@PathVariable long id) {
    Long interviewId = interviewRequestService.getInterviewIdByTimeSlotId(id);
    return (interviewId != null)
        ? ResponseEntity.ok(interviewId)
        : ResponseEntity.noContent().build();
  }

  /**
   * Returns the interview history ID associated with the given time slot ID.
   *
   * @param id the ID of the time slot
   * @return the ID of the interview history, or {@code 204 status} - no interview history is linked
   */
  @GetMapping("/timeslots/{id}/interview-history-id")
  public ResponseEntity<Long> getInterviewHistoryIdByTimeSlot(@PathVariable long id) {
    Long interviewId = interviewRequestService.getInterviewHistoryIdByTimeSlotId(id);
    return (interviewId != null)
        ? ResponseEntity.ok(interviewId)
        : ResponseEntity.noContent().build();
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
  @PatchMapping("/{id}")
  public void update(@PathVariable long id,
      @Valid @RequestBody InterviewRequestDto interviewRequest) {
    interviewRequestService.update(id, interviewRequest);
  }

  /**
   * Add time slots to the specific interview request.
   *
   * @param id        the interview request ID
   * @param dateTimes list of the time slots that should be added
   */
  @PostMapping("/{id}/add-timeslots")
  public void addTimeSlots(@PathVariable long id,
      @Valid @RequestBody List<ZonedDateTime> dateTimes) {
    interviewRequestService.addTimeSlots(id, dateTimes);
  }

  /**
   * Delete time slots to the specific interview request.
   *
   * @param id        the interview request ID
   * @param dateTimes list of the time slots that should be deleted
   */
  @DeleteMapping("/{id}/delete-timeslots")
  public void deleteTimeSlots(@PathVariable long id,
      @Valid @RequestBody List<ZonedDateTime> dateTimes) {
    interviewRequestService.deleteTimeSlots(id, dateTimes);
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
