package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user entities in the system. This service provides methods for
 * creating, retrieving, updating, and deleting user entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

  /**
   * Repository for accessing user data in the database.
   */
  private final UserRepository userRepository;

  /**
   * Checks if a user with the given email exists in the system.
   *
   * @param email The email address of the user to check.
   * @return True if a user with the given email exists, false otherwise.
   */
  public boolean isUserExistByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Saves the user entity.
   *
   * @param user The user entity to save.
   * @return The saved user entity.
   */
  public User save(User user) {
    return userRepository.save(user);
  }

  /**
   * Get the user entity by id.
   *
   * @param id The user id.
   * @return The user entity that has been found.
   */
  public User getById(long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("The user cannot be found."));
  }

  /**
   * Finds a user by email.
   *
   * @param email The email address of the user to find.
   * @return The user entity if found, otherwise throws UsernameNotFoundException.
   * @throws UsernameNotFoundException if the user with the given email is not found.
   */
  public User findUserByEmail(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email).orElseThrow();
  }
}
