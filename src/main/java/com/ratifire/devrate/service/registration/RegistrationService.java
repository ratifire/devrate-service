package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import com.ratifire.devrate.service.RoleService;
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

  private final RoleService roleService;

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
   * Registers a new user.
   *
   * <p>This method registers a new user based on the provided SignUpDto. It checks if the user
   * already exists by email, throws an exception if the user is already registered, creates a new
   * user entity, sends an email confirmation code for user verification, and returns the registered
   * user information.
   *
   * @param signUpDto The SignUpDto containing the user information to be registered.
   * @return SignUpDto representing the registered user information.
   * @throws UserAlreadyExistException If a user with the provided email already exists.
   */
  @Transactional
  public SignUpDto registerUser(SignUpDto signUpDto) {
    if (isUserExistByEmail(signUpDto.getEmail())) {
      throw new UserAlreadyExistException("User is already registered!");
    }

    Role role = roleService.getDefaultRole();
    User user = userService.save(userMapper.toEntity(signUpDto, role));

    EmailConfirmationCode savedEmailConfirmationCode = emailConfirmationCodeService.save(
        user.getId());

    SimpleMailMessage confirmationMessage = emailConfirmationCodeService.createMessage(
        user.getEmail(),
        savedEmailConfirmationCode.getCode());

    emailService.sendEmail(confirmationMessage, true);

    return userMapper.toDto(user);
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
