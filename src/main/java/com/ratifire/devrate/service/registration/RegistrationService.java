package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.email.EmailService;
import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
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

  private final EmailConfirmationCodeService emailConfirmationCodeService;

  private final EmailService emailService;

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
   * user entity persists it to the database, generate email confirmation code, and send it by
   * email.
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
        .role(roleService.getRoleByName("ROLE_USER"))
        .build();
    userSecurityService.save(userSecurity);

    EmailConfirmationCode savedEmailConfirmationCode =
            emailConfirmationCodeService.save(user.getId());

    SimpleMailMessage confirmationMessage  = emailConfirmationCodeService
        .createMessage(user.getEmail(), savedEmailConfirmationCode.getCode());

    emailService.sendEmail(confirmationMessage, true);

    return user;
  }

  /**
   * Checks if the provided confirmation code matches the stored confirmation code for the
   * user and marks the user as verified if the codes match.
   *
   * @param userId The unique identifier of the user for whom the confirmation code is checked.
   * @param code   The confirmation code to be checked against the stored code.
   * @return {@code true} if the confirmation code matches and the user is marked as verified,
   *         {@code false} otherwise.
   * @throws EmailConfirmationCodeException If there are issues with the confirmation code
   *                                        such as missing or invalid codes.
   */
  @Transactional
  public boolean isCodeConfirmed(long userId, String code) {
    if (StringUtil.isEmpty(code)) {
      throw new IllegalArgumentException("The confirmation code is a required argument");
    }

    EmailConfirmationCode emailConfirmationCode = emailConfirmationCodeService
            .getEmailConfirmationCodeByUserId(userId);

    if (StringUtil.isEmpty(emailConfirmationCode.getCode())) {
      throw new EmailConfirmationCodeException("The confirmation code for user ID \""
              + userId + " is missing or empty.");
    }

    if (emailConfirmationCode.getCode().equals(code)) {
      User user = userService.getById(userId);
      user.setVerified(true);
      userService.save(user);

      emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());

      return true;
    }

    throw new EmailConfirmationCodeException("The confirmation code is invalid.");
  }
}
