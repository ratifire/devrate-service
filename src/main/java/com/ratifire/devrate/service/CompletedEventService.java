package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.CompletedEventDto;
import com.ratifire.devrate.entity.CompletedEvent;
import com.ratifire.devrate.repository.CompletedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling completed event operations.
 */
@Service
@RequiredArgsConstructor
public class CompletedEventService {

  private final CompletedEventRepository repository;

  /**
   * Retrieves the details of a completed event by its event ID.
   *
   * @param eventId the ID of the event to retrieve
   * @return a CompletedEventDto containing the event details
   */
  public CompletedEventDto getCompletedEventDetails(long eventId) {
    CompletedEvent event = repository.findByEventId(eventId)
        .orElseThrow(() -> new IllegalStateException(
            "Completed event with eventId " + eventId + " not found"));

    return CompletedEventDto.builder()
        .id(event.getId())
        .eventId(event.getEventId())
        .build();
  }
}