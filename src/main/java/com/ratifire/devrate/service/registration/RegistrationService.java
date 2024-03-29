package com.ratifire.devrate.service.registration;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.userinfo.UserInfoService;
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
   * <p>This method registers a new user based on the provided SignUpDto. It checks if the user
   * already exists by email, throws an exception if the user is already registered, creates a new
   * user entity, sends an email confirmation code for user verification, and returns the registered
   * user information.
   *
   * @param userDto The SignUpDto containing the user information to be registered.
   * @return SignUpDto representing the registered user information.
   * @throws UserAlreadyExistException If a user with the provided email already exists.
   */
  @Transactional
  public UserDto registerUser(UserDto userDto) {
    if (isUserExistByEmail(userDto.getEmail())) {
      throw new UserAlreadyExistException("User is already registered!");
    }

    Role role = roleService.getDefaultRole();
    User user = userService.save(userMapper.toEntity(userDto, role));

    UserInfoDto userInfoDto = UserInfoDto.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .country(user.getCountry())
        .subscribed(user.isSubscribed())
        .userId(user.getId())
        .build();
    userInfoService.create(userInfoDto);

    EmailConfirmationCode savedEmailConfirmationCode =
            emailConfirmationCodeService.save(user.getId());

    SimpleMailMessage confirmationMessage = emailConfirmationCodeService
        .createMessage(user.getEmail(), savedEmailConfirmationCode.getCode());

    emailService.sendEmail(confirmationMessage, true);

    return userMapper.toDto(user);
  }

  /**
   * Checks if the provided confirmation code matches the stored confirmation code for the
   * user and marks the user as verified if the codes match.
   *
   * @param confirmationCode The confirmation code provided by the user.
   * @return The ID of the user whose registration has been confirmed
   * @throws IllegalArgumentException If the provided confirmation code is empty or null.
   */
  @Transactional
  public long confirmRegistration(String confirmationCode) {
    if (StringUtil.isEmpty(confirmationCode)) {
      throw new IllegalArgumentException("The confirmation code is a required argument");
    }

    EmailConfirmationCode emailConfirmationCode = emailConfirmationCodeService
        .findEmailConfirmationCode(confirmationCode);

    User user = userService.getById(emailConfirmationCode.getUserId());
    user.setVerified(true);
    userService.save(user);

    emailConfirmationCodeService.deleteConfirmedCode(emailConfirmationCode.getId());

    return user.getId();
  }
}
