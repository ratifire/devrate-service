package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.CompletedEventDto;
import com.ratifire.devrate.service.CompletedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing completed events.
 */
@RestController
@RequestMapping("/completed-events")
@RequiredArgsConstructor
public class CompletedEventController {

  private final CompletedEventService completedEventService;

  /**
   * Retrieves the details of a completed event by its event ID.
   *
   * @param id the event ID of the completed event
   * @return a CompletedEventDto containing details of the event
   */
  @GetMapping("/events/{id}")
  public CompletedEventDto getCompletedEventDetails(@PathVariable long id) {
    return completedEventService.getCompletedEventDetails(id);
  }
}