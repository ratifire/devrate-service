package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.EventType;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.EventRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.util.DateTimeUtils;
import jakarta.transaction.Transactional;
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

  private final UserService userService;
  private final UserContextProvider userContextProvider;
  private final EventRepository eventRepository;
  private final DataMapper<EventDto, Event> eventMapper;

  /**
   * Retrieves a list of events for a specified user that occur within a given time range.
   *
   * @param from the start of the time range (inclusive)
   * @param to   the end of the time range (inclusive)
   * @return a list of {@link EventDto} objects representing the events for the user
   */
  public List<EventDto> findBetweenDateTime(ZonedDateTime from, ZonedDateTime to) {
    User user = userService.findById(userContextProvider.getAuthenticatedUserId());
    return user.getEvents().stream()
        .filter(event -> DateTimeUtils.isWithinRange(event.getStartTime(), from, to))
        .map(eventMapper::toDto)
        .toList();
  }

  /**
   * Saves a list of Event objects for each attendee by cloning the provided event and setting the
   * userId for each attendee.
   *
   * @param event     the Event object to be cloned for each attendee
   * @param attendees a list of user IDs representing the attendees
   */
  @Transactional
  public long save(Event event, List<Long> attendees) {
    List<User> users = userService.findByIds(attendees);
    users.forEach(user -> user.getEvents().add(event));

    userService.saveAll(users);
    return eventRepository.save(event).getId();
  }

  /**
   * Deletes an event by its associated interview ID.
   *
   * @param eventId the ID of the event to be deleted
   */
  @Transactional
  public void delete(long eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new IllegalStateException("Could not find event with id: " + eventId));

    List<User> users = userService.findAllByEventsContaining(event);
    users.forEach(user -> user.getEvents().remove(event));
    userService.saveAll(users);
    eventRepository.delete(event);
  }

  /**
   * Builds an Event object with the provided details for an interview.
   *
   * @return an Event object containing the specified details
   */
  public Event buildEvent(long candidateId, long interviewerId, String roomUrl,
      ZonedDateTime date, String title) {
    return Event.builder()
        .type(EventType.INTERVIEW)
        .roomLink(roomUrl)
        .hostId(interviewerId)
        .participantIds(List.of(candidateId))
        .startTime(date)
        .title(title)
        .build();

  }
}
