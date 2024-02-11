package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user entities in the system.
 * This service provides methods for creating, retrieving, updating, and deleting user entities.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * Repository for accessing user data in the database.
     */
    private final UserRepository userRepository;

    /**
     * Checks if a user with the given email address exists.
     * @param email The email address to check for existence.
     * @return True if a user with the specified email address exists, false otherwise.
     */
    public boolean isExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Saves the user entity.
     * @param user The user entity to save.
     * @return The saved user entity.
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
