package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.service.EventService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles HTTP requests related to event management.
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;

  /**
   * Retrieves a list of events for a specified user that occur within a given date range.
   *
   * @param from the start of the date range (inclusive)
   * @param to   the end of the date range (inclusive)
   * @return a list of {@link EventDto} objects representing the events for the user
   */
  @GetMapping()
  public List<EventDto> findEventsBetweenDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return eventService.findBetweenDate(from, to);
  }

  /**
   * Retrieves a list of events for a given user that start from a specified date and time.
   *
   * @param from the starting date and time in ISO format (e.g., {@code 2024-08-28T12:00:00Z})
   * @return a list of {@link EventDto} objects representing the events starting from
   */
  @GetMapping("/closest")
  public List<EventDto> findEventsFromDateTime(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from) {
    return eventService.findFromDateTime(from);
  }
}