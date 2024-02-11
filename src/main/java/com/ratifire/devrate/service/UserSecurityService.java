package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user security information in the system.
 * This service provides methods for creating, retrieving, updating, and deleting user security information.
 */
@Service
@RequiredArgsConstructor
public class UserSecurityService {

    /**
     * Repository for accessing user security data in the database.
     */
    private final UserSecurityRepository userSecurityRepository;

    /**
     * Saves the user security information.
     * @param userSecurity The user security entity to save.
     * @return The saved user security entity.
     */
    public UserSecurity save(UserSecurity userSecurity) {
        return userSecurityRepository.save(userSecurity);
    }
}
