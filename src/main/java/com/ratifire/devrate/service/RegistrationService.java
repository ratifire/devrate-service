package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for user registration logic. This service handles the registration
 * process, including validation and database operations.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

  /**
   * Service responsible for user management operations.
   */
  private final UserService userService;

  /**
   * Service responsible for role management operations.
   */
  private final RoleService roleService;

  /**
   * Service responsible for user security management operations.
   */
  private final UserSecurityService userSecurityService;

  private final UserMapper userMapper;

  /**
   * Service responsible for sending message to the user.
   */
  private final GMailSendService gmailSendService;

  /**
   * Checks if a user with the given email address exists.
   *
   * @param email The email address to check for existence.
   * @return True if a user with the specified email address exists, false otherwise.
   */
  public boolean isUserExistByEmail(String email) {
    return userService.isUserExistByEmail(email);
  }

  /**
   * Method for registering a new user. This method validates the sign-up request, creates a new
   * user entity, and persists it to the database.
   *
   * @param signUpDto DTO containing user sign-up data.
   * @return The registered User object.
   */
  @Transactional
  public User registerUser(SignUpDto signUpDto) {
    if (isUserExistByEmail(signUpDto.getEmail())) {
      throw new UserAlreadyExistException("User is already registered!");
    }

    User user = userService.save(userMapper.toEntity(signUpDto));

    UserSecurity userSecurity = UserSecurity.builder()
            .password(signUpDto.getPassword())
            .userId(user.getId())
            .userRoleId(roleService.getRoleByName("ROLE_USER").getId())
            .build();
    userSecurityService.save(userSecurity);

    sendMailAboutSuccessfulRegistration(signUpDto);

    return user;
  }

  /**
   * Method for sending the message to the user about successful registration.
   */
  public void sendMailAboutSuccessfulRegistration(SignUpDto signUpDto) {
    String message = String.format("""
            Dear %s %s,
                                    
            Registration is successful!
                                            
            Welcome to our platform!
            """, signUpDto.getFirstName(), signUpDto.getLastName());

    try {
      gmailSendService.sendMail("Registration is successful", message, signUpDto.getEmail());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
