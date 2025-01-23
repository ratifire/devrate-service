package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EventHistoryDto;
import com.ratifire.devrate.entity.EventHistory;
import com.ratifire.devrate.repository.EventHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling completed event operations.
 */
@Service
@RequiredArgsConstructor
public class EventHistoryService {

  private final EventHistoryRepository repository;

  /**
   * Retrieves the event history by its event ID.
   *
   * @param eventId the ID of the event to retrieve
   * @return a EventHistoryDto containing the event history
   */
  public EventHistoryDto findByEventId(long eventId) {
    EventHistory history = repository.findByEventId(eventId)
        .orElseThrow(() -> new IllegalStateException(
            "Event history with eventId " + eventId + " not found"));

    return EventHistoryDto.builder()
        .id(history.getId())
        .eventId(history.getEventId())
        .build();
  }
}