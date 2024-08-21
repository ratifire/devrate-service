package com.ratifire.devrate.service.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.EventByTypeIdNotFoundException;
import com.ratifire.devrate.repository.EventRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Event tests for the {@link EventService} class.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @InjectMocks
  private EventService eventService;

  @Mock
  private EventRepository eventRepository;

  @Mock
  private UserRepository userRepository;

  private Event event;
  private User host;
  private User participant;

  @BeforeEach
  void setUp() {
    eventService.setUserRepository(userRepository);

    event = new Event();
    List<Event> events = new ArrayList<>();
    events.add(event);
    host = User.builder()
        .events(events)
        .build();
    participant = User.builder()
        .events(events)
        .build();
  }

  @Test
  void save_ShouldSaveEventAndUpdateUsers() {
    List<User> attendees = Arrays.asList(host, participant);

    eventService.save(event, attendees);

    assertTrue(host.getEvents().contains(event));
    assertTrue(participant.getEvents().contains(event));
  }

  @Test
  void deleteByEventTypeId_ShouldDeleteEventAndUpdateUsers_WhenEventIsFound() {
    long eventTypeId = 1L;
    List<User> attendees = Arrays.asList(host, participant);

    when(eventRepository.findByEventTypeId(eventTypeId)).thenReturn(Optional.of(event));
    when(userRepository.findAllByEventsContaining(event)).thenReturn(attendees);

    eventService.deleteByEventTypeId(eventTypeId);

    assertFalse(host.getEvents().contains(event));
    assertFalse(participant.getEvents().contains(event));
  }

  @Test
  void deleteByEventTypeId_ShouldThrowException_WhenEventIsNotFound() {
    long eventTypeId = 1L;

    when(eventRepository.findByEventTypeId(eventTypeId)).thenReturn(Optional.empty());

    assertThrows(EventByTypeIdNotFoundException.class,
        () -> eventService.deleteByEventTypeId(eventTypeId));
  }
}