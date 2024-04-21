package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.MailConfirmationCodeExpiredException;
import com.ratifire.devrate.exception.MailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.UserSecurityAlreadyExistException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.user.UserService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

  private static final long CONFIRM_CODE_EXPIRATION_HOURS = 24;
  private final UserSecurityService userSecurityService;
  private final RoleService roleService;
  private final DataMapper<UserRegistrationDto, UserSecurity> userMapper;
  private final EmailConfirmationCodeService emailConfirmationCodeService;
  private final EmailService emailService;
  private final UserService userService;
  private final NotificationService notificationService;

  /**
   * Checks if a user security with the given email address exists.
   *
   * @param email The email address to check for existence.
   * @return True if a user security with the specified email address exists, false otherwise.
   */
  public boolean isUserExistByEmail(String email) {
    return userSecurityService.isExistByEmail(email);
  }

  /**
   * Registers a new user.
   *
   * <p>This method registers a new user based on the provided {@link UserRegistrationDto}. It
   * checks if the user already exists by email, throws an exception if the user is already
   * registered, creates a new user entity, sends an email confirmation code for user verification,
   * and returns the registered user information.
   *
   * @param userRegistrationDto Containing the user information to be registered.
   * @throws UserSecurityAlreadyExistException If a user with the provided email already exists.
   */
  @Transactional
  public void registerUser(UserRegistrationDto userRegistrationDto) {
    String email = userRegistrationDto.getEmail();
    if (isUserExistByEmail(email)) {
      throw new UserSecurityAlreadyExistException("User is already registered!");
    }

    UserDto userDto = UserDto.builder()
        .firstName(userRegistrationDto.getFirstName())
        .lastName(userRegistrationDto.getLastName())
        .country(userRegistrationDto.getCountry())
        .subscribed(userRegistrationDto.isSubscribed())
        .build();
    User createdUser = userService.create(userDto);

    UserSecurity entity = userMapper.toEntity(userRegistrationDto);
    entity.setRole(roleService.getDefaultRole());
    entity.setUser(createdUser);
    UserSecurity userSecurity = userSecurityService.save(entity);

    String code = emailConfirmationCodeService.getConfirmationCode(userSecurity.getId());
    emailService.sendConfirmationCodeEmail(email, code);
  }

  /**
   * Checks if the provided confirmation code matches the stored confirmation code for the user and
   * marks the user as verified if the codes match.
   *
   * @param confirmationCode The confirmation code provided by the user.
   * @return The ID of the user whose registration has been confirmed
   * @throws MailConfirmationCodeRequestException If the provided confirmation code is empty or
   *                                               null.
   * @throws MailConfirmationCodeExpiredException If the confirmation code is expired.
   */
  public long confirmRegistration(String confirmationCode) {
    if (StringUtil.isEmpty(confirmationCode)) {
      throw new MailConfirmationCodeRequestException("The confirmation code is a required "
          + "argument");
    }

    EmailConfirmationCode emailConfirmationCode = emailConfirmationCodeService
        .findEmailConfirmationCode(confirmationCode);

    // Check if the confirmation code has expired
    LocalDateTime createdAt = emailConfirmationCode.getCreatedAt();
    LocalDateTime currentDateTime = LocalDateTime.now();
    long hoursSinceCreation = ChronoUnit.HOURS.between(createdAt, currentDateTime);
    if (hoursSinceCreation >= CONFIRM_CODE_EXPIRATION_HOURS) {
      emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());
      throw new MailConfirmationCodeExpiredException("The confirmation code has expired");
    }

    UserSecurity userSecurity = userSecurityService
        .getById(emailConfirmationCode.getUserSecurityId());
    userSecurity.setVerified(true);
    userSecurityService.save(userSecurity);

    emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());

    sendGreetings(userSecurity.getUser(), userSecurity.getEmail());
    return userSecurity.getId();
  }

  /**
   * Sends greetings to a user via email and adds a greeting notification.
   *
   * @param user  The user to whom greetings are sent.
   * @param email The email address of the user.
   */
  private void sendGreetings(User user, String email) {
    notificationService.addGreetingNotification(user);
    emailService.sendGreetingsEmail(user, email);
  }
}
