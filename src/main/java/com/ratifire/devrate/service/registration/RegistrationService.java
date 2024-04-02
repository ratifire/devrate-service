package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.EmailConfirmationCodeExpiredException;
import com.ratifire.devrate.exception.EmailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.userinfo.UserInfoService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

  private static final long CONFIRM_CODE_EXPIRATION_HOURS  = 24;
  /**
   * Service responsible for user management operations.
   */
  private final UserService userService;

  private final RoleService roleService;

  private final DataMapper<UserDto, UserSecurity> userMapper;

  private final EmailConfirmationCodeService emailConfirmationCodeService;

  private final EmailService emailService;

  private final UserInfoService userInfoService;

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
   * <p>This method registers a new user based on the provided UserDto. It checks if the user
   * already exists by email, throws an exception if the user is already registered, creates a new
   * user entity, sends an email confirmation code for user verification, and returns the registered
   * user information.
   *
   * @param userDto The UserDto containing the user information to be registered.
   * @return UserDto representing the registered user information.
   * @throws UserAlreadyExistException If a user with the provided email already exists.
   */
  @Transactional
  public UserDto registerUser(UserDto userDto) {
    if (isUserExistByEmail(userDto.getEmail())) {
      throw new UserAlreadyExistException("User is already registered!");
    }

    UserSecurity entity = userMapper.toEntity(userDto);
    entity.setRole(roleService.getDefaultRole());

    UserSecurity userSecurity = userService.save(entity);

    UserInfoDto userInfoDto = UserInfoDto.builder()
        .firstName(userDto.getFirstName())
        .lastName(userDto.getLastName())
        .country(userDto.getCountry())
        .subscribed(userDto.isSubscribed())
        .userId(userSecurity.getId())
        .build();
    userInfoService.create(userInfoDto);

    EmailConfirmationCode savedEmailConfirmationCode =
            emailConfirmationCodeService.save(userSecurity.getId());

    SimpleMailMessage confirmationMessage = emailConfirmationCodeService
        .createMessage(userSecurity.getEmail(), savedEmailConfirmationCode.getCode());

    emailService.sendEmail(confirmationMessage, true);

    return userMapper.toDto(userSecurity);
  }

  /**
   * Checks if the provided confirmation code matches the stored confirmation code for the
   * user and marks the user as verified if the codes match.
   *
   * @param confirmationCode The confirmation code provided by the user.
   * @return The ID of the user whose registration has been confirmed
   * @throws EmailConfirmationCodeRequestException If the provided confirmation code is empty or
   *                                               null.
   * @throws EmailConfirmationCodeExpiredException If the confirmation code is expired.
   */
  public long confirmRegistration(String confirmationCode) {
    if (StringUtil.isEmpty(confirmationCode)) {
      throw new EmailConfirmationCodeRequestException("The confirmation code is a required "
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
      throw new EmailConfirmationCodeExpiredException("The confirmation code has expired");
    }

    UserSecurity userSecurity = userService.getById(emailConfirmationCode.getUserId());
    userSecurity.setVerified(true);
    userService.save(userSecurity);

    emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());

    return userSecurity.getId();
  }
}
