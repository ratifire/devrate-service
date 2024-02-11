package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.UserRepository;
import com.ratifire.devrate.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user registration logic. This service handles the registration
 * process, including validation and database operations.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

  /**
   * Repository for accessing user data in the database.
   */
  private final UserRepository userRepository;

  /**
   * Service responsible for role management operations.
   */
  private final RoleService roleService;

  /**
   * Repository for accessing user security data in the database.
   */
  private final UserSecurityRepository userSecurityRepository;

  /**
   * Checks if a user with the given email address exists.
   *
   * @param email The email address to check for existence.
   * @return True if a user with the specified email address exists, false otherwise.
   */
  public boolean isUserExistByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  /**
   * Method for registering a new user.
   * This method validates the sign-up request, creates a new user entity,
   * and persists it to the database.
   *
   * @param signUpDto DTO containing user sign-up data.
   * @return The registered User object.
   */
  public User registerUser(SignUpDto signUpDto) {
    User user = userRepository.save(signUpDto.toUser());

    UserSecurity userSecurity = UserSecurity.builder()
        .password(signUpDto.getPassword())
        .userId(user.getId())
        .userRoleId(roleService.getRoleByName("ROLE_USER").getId())
        .build();
    userSecurityRepository.save(userSecurity);

    return user;
  }
}
