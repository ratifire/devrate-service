package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserSecurityNotFoundException;
import com.ratifire.devrate.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user entities in the system. This service provides methods for
 * creating, retrieving, updating, and deleting user entities.
 */
@Service
@RequiredArgsConstructor
public class UserSecurityService {

  /**
   * Repository for accessing user data in the database.
   */
  private final UserSecurityRepository userSecurityRepository;

  /**
   * Checks if a user with the given email exists in the system.
   *
   * @param email The email address of the user to check.
   * @return True if a user with the given email exists, false otherwise.
   */
  public boolean isUserExistByEmail(String email) {
    return userSecurityRepository.existsByEmail(email);
  }

  /**
   * Saves the user entity.
   *
   * @param userSecurity The user entity to save.
   * @return The saved user security entity.
   */
  public UserSecurity save(UserSecurity userSecurity) {
    return userSecurityRepository.save(userSecurity);
  }

  /**
   * Get the user security entity by id.
   *
   * @param id The user security id.
   * @return The user security entity that has been found.
   */
  public UserSecurity getById(long id) {
    return userSecurityRepository.findById(id)
            .orElseThrow(() -> new UserSecurityNotFoundException("The user cannot be found."));
  }

  /**
   * Finds a user by email.
   *
   * @param email The email address of the user to find.
   * @return The user security entity if found, otherwise throws UsernameNotFoundException.
   * @throws UsernameNotFoundException if the user with the given email is not found.
   */
  public UserSecurity findUserByEmail(String email) throws UsernameNotFoundException {
    return userSecurityRepository.findByEmail(email).orElseThrow();
  }
}
