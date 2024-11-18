package com.ratifire.devrate.security.service;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.ContactType;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.exception.UserRegistrationException;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for user registration logic. This service handles the registration
 * process, including validation and database operations.
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

  private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
  private final CognitoApiClientService cognitoApiClientService;
  private final UserService userService;
  private final EmailService emailService;
  private final NotificationService notificationService;
  private final PasswordEncoder passwordEncoder;

  /**
   * Registers a new user in the system and AWS Cognito.
   *
   * @param userRegistrationDto the DTO containing the user's registration details.
   */
  @Transactional
  public void registerUser(UserRegistrationDto userRegistrationDto) {
    String email = userRegistrationDto.getEmail();
    String password = userRegistrationDto.getPassword();
    UserDto userDto = UserDto.builder()
        .firstName(userRegistrationDto.getFirstName())
        .lastName(userRegistrationDto.getLastName())
        .country(userRegistrationDto.getCountry())
        .subscribed(userRegistrationDto.isSubscribed())
        .build();

    if (userService.existsByEmail(email)) {
      log.error("User with email {} already exists.", email);
      throw new UserAlreadyExistsException("User with email " + email + " already exists.");
    }

    try {
      User user = userService.create(userDto, email, " ");
      cognitoApiClientService.register(email, password, user.getId(), AccessLevel.getDefaultRole());
      user.setPassword(passwordEncoder.encode(password));
      userService.updateUser(user);
    } catch (Exception e) {
      log.error("Initiate registration process was failed for email {}: {}",
          userRegistrationDto.getEmail(), e.getMessage(), e);
      throw new UserRegistrationException("Initiate registration process was failed.");
    }
  }

  /**
   * Confirms user registration and finalizes the account setup process.
   *
   * @param confirmationCodeDto the DTO containing the user's email and the confirmation code
   *                            required for registration.
   */
  @Transactional
  public long confirmRegistration(ConfirmRegistrationDto confirmationCodeDto) {
    try {
      String email = confirmationCodeDto.getEmail();
      String code = confirmationCodeDto.getConfirmationCode();
      cognitoApiClientService.confirmRegistration(email, code);
      User user = userService.findByEmail(email);
      Contact contact = Contact
          .builder()
          .type(ContactType.EMAIL)
          .value(email)
          .build();
      user.getContacts()
          .add(contact);
      sendGreetings(user, email);
      return user.getId();
    } catch (Exception e) {
      log.error("Confirmation registration process was failed for email {}: {}",
          confirmationCodeDto.getEmail(), e.getMessage(), e);
      throw new UserRegistrationException("Confirmation registration process was failed.");
    }
  }

  /**
   * Sends greetings to a user via email and adds a greeting notification.
   *
   * @param user  The user to whom greetings are sent.
   * @param email The email address of the user.
   */
  private void sendGreetings(User user, String email) {
    notificationService.addGreetingNotification(user, email);
    emailService.sendGreetingsEmail(user, email);
  }
}
