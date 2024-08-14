package com.ratifire.devrate.service.event;

import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
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
    attendees.forEach(user -> {
      if (user != null && user.getEvents() != null) {
        user.getEvents().add(event);
      }
    });

    userRepository.saveAll(attendees);
    eventRepository.save(event);
  }
}
