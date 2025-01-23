package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.InterviewHistoryDto;
import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.service.interview.InterviewHistoryService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to InterviewHistory information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/interview-histories")
public class InterviewHistoryController {

  private final InterviewHistoryService interviewHistoryService;

  /**
   * Retrieves a list of user`s interview summaries information by user ID.
   *
   * @return the list of user's InterviewSummary information as a DTO
   */
  @GetMapping()
  public Page<InterviewHistoryDto> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return interviewHistoryService.getAllByUserId(page, size);
  }

  /**
   * Retrieves an interview history information by ID.
   *
   * @return the Interview history information as a DTO
   */
  @GetMapping("/{id}")
  public InterviewHistoryDto getById(@PathVariable long id) {
    return interviewHistoryService.findByIdAndUserId(id);
  }

  /**
   * Deletes the association between a user and an interview summary.
   *
   * @param id the ID of the interview summary to be removed from the user's associations
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    interviewHistoryService.delete(id);
  }

}
