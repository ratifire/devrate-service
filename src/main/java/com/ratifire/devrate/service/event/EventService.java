package com.ratifire.devrate.service.event;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.EventByTypeIdNotFoundException;
import com.ratifire.devrate.repository.EventRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling Event operations.
 */
@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  private UserRepository userRepository;

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Saves an event and updates the event list for each attendee.
   *
   * @param event       the event to be saved
   * @param attendees   the list of users who will attend the event
   */
  @Transactional
  public void save(Event event, List<User> attendees) {
    attendees.forEach(user -> user.getEvents().add(event));

    userRepository.saveAll(attendees);
    eventRepository.save(event);
  }

  /**
   * Deletes an event by its associated interview ID and updates all users who have this event.
   *
   * @param eventTypeId the ID of the interview associated with the event to be deleted
   * @throws EventByTypeIdNotFoundException if no event with the given interview ID is found
   */
  @Transactional
  public void deleteByEventTypeId(long eventTypeId) {
    Event event = eventRepository.findByEventTypeId(eventTypeId)
        .orElseThrow(() -> new EventByTypeIdNotFoundException(eventTypeId));

    List<User> users = userRepository.findAllByEventsContaining(event);
    users.forEach(user -> user.getEvents().remove(event));

    userRepository.saveAll(users);
    eventRepository.delete(event);
  }
}
