package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.dto.EmailConfirmationCodeDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for user registration logic. This service handles the registration
 * process, including validation and database operations.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

  public static final String EMAIL_CONFIRMATION_SUBJECT = "Confirm your email";

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
        .userRoleId(roleService.getRoleByName("ROLE_USER").getId())
        .build();
    userSecurityService.save(userSecurity);

    EmailConfirmationCode savedEmailConfirmationCode =
            emailConfirmationCodeService.save(user.getId());

    String messageText = buildConfirmationEmailText(user.getEmail(),
            savedEmailConfirmationCode.getCode());

    emailService.sendEmail(EMAIL_CONFIRMATION_SUBJECT, user.getEmail(), messageText, false);

    return user;
  }

  private String buildConfirmationEmailText(String email, String code) {
    return String.format("""
            Hi %s,
            Thank you for registering. Please use the code below to activate your account:
            Activation code: %s""", email, code);
  }

  /**
   * Checks if the provided confirmation code in the {@link EmailConfirmationCodeDto}
   * matches the stored confirmation code for the user and marks the user as verified if the
   * codes match.
   *
   * @param emailConfirmationCodeDto The DTO containing the confirmation code and user ID.
   * @return {@code true} if the confirmation code matches and the user is marked as verified,
   *         {@code false} otherwise.
   * @throws EmailConfirmationCodeException If there are issues with the confirmation code
   *                                        such as missing or invalid codes.
   */
  @Transactional
  public boolean isCodeConfirmed(EmailConfirmationCodeDto emailConfirmationCodeDto) {
    long userId = emailConfirmationCodeDto.getUserId();

    EmailConfirmationCode emailConfirmationCode = emailConfirmationCodeService
            .getEmailConfirmationCodeByUserId(userId);

    if (StringUtil.isEmpty(emailConfirmationCode.getCode())) {
      throw new EmailConfirmationCodeException("The confirmation code for user ID \""
              + userId + " is missing or empty.");
    }

    if (emailConfirmationCode.getCode().equals(emailConfirmationCodeDto.getCode())) {
      User user = userService.getById(userId);
      user.setVerified(true);
      userService.save(user);

      emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());

      return true;
    }

    throw new EmailConfirmationCodeException("The confirmation code is invalid.");
  }
}
