package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EventHistoryDto;
import com.ratifire.devrate.service.EventHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing completed events.
 */
@RestController
@RequestMapping("/event-histories")
@RequiredArgsConstructor
public class EventHistoryController {

  private final EventHistoryService service;

  /**
   * Retrieves the event history by its event ID.
   *
   * @param id the ID of the completed event
   * @return a EventHistoryDto containing event history information
   */
  @GetMapping("/events/{id}")
  public EventHistoryDto findByEventId(@PathVariable long id) {
    return service.findByEventId(id);
  }
}