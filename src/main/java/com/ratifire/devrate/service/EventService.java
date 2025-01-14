package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.enums.EventType;
import com.ratifire.devrate.repository.EventRepository;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling Event operations.
 */
@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  /**
   * Saves a list of Event objects for each attendee by cloning the provided event and setting the
   * userId for each attendee.
   *
   * @param event     the Event object to be cloned for each attendee
   * @param attendees a list of user IDs representing the attendees
   */
  public void save(Event event, List<Long> attendees) {
    List<Event> eventsToSave = attendees.stream()
        .map(userId -> event.toBuilder()
            .userId(userId)
            .build())
        .toList();

    eventRepository.saveAll(eventsToSave);
  }

  /**
   * Deletes an event by its associated interview ID.
   *
   * @param eventTypeId the ID of the interview associated with the event to be deleted
   */
  public void deleteAllByEventTypeId(long eventTypeId) {
    eventRepository.deleteByEventTypeId(eventTypeId);
  }

  /**
   * Builds an Event object with the provided details for an interview.
   *
   * @return an Event object containing the specified details
   */
  public Event buildEvent(long interviewId, long candidateId, long interviewerId, String roomUrl,
      ZonedDateTime date) {
    return Event.builder()
        .type(EventType.INTERVIEW)
        .roomLink(roomUrl)
        .hostId(interviewerId)
        .participantIds(List.of(candidateId))
        .startTime(date)
        .eventTypeId(interviewId)
        .build();

  }
}
