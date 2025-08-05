package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.enums.EventType;
import com.ratifire.devrate.repository.EventRepository;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.util.DateTimeUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
  private final InterviewRepository interviewRepository;

  /**
   * Retrieves a list of events for a specified user that occur within a given time range.
   *
   * @param from the start of the time range (inclusive)
   * @param to   the end of the time range (inclusive)
   * @return a list of {@link EventDto} objects representing the events for the user
   */
  public List<EventDto> findBetweenDateTime(LocalDate from, LocalDate to) {
    User authUser = userService.findById(userContextProvider.getAuthenticatedUserId());
    List<Event> userEvents = authUser.getEvents();

    if (userEvents.isEmpty()) {
      return List.of();
    }

    List<Event> filteredEvents = userEvents.stream()
        .filter(event -> DateTimeUtils.isWithinRange(event.getStartTime(), from, to))
        .toList();

    if (filteredEvents.isEmpty()) {
      return List.of();
    }

    Map<Long, Interview> interviewByEventId = interviewRepository
        .findByEventIdIn(filteredEvents.stream().map(Event::getId).toList())
        .stream()
        .filter(interview -> interview.getUserId() == authUser.getId())
        .collect(Collectors.toMap(
            Interview::getEventId,
            interview -> interview,
            (a, b) -> b
        ));

    return filteredEvents.stream()
        .map(event -> buildEventDto(event, interviewByEventId.get(event.getId())))
        .filter(Objects::nonNull)
        .toList();
  }

  private EventDto buildEventDto(Event event, Interview interview) {
    if (interview == null) {
      return null;
    }

    return EventDto.builder()
        .id(event.getId())
        .type(event.getType())
        .hostId(event.getHostId())
        .roomLink(event.getRoomLink())
        .startTime(event.getStartTime())
        .title(event.getTitle())
        .interviewId(interview.getId())
        .role(interview.getRole())
        .build();
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
