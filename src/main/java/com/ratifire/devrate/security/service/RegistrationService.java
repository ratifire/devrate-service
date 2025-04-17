package com.ratifire.devrate.security.service;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.ContactType;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.exception.UserRegistrationException;
import com.ratifire.devrate.security.model.dto.ConfirmRegistrationDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.dto.UserRegistrationDto;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for user registration logic. This service handles the registration
 * process, including validation and database operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!local")
public class RegistrationService {

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
        .build();

    if (userService.existsByEmail(email)) {
      log.error("User with email {} already exists.", email);
      throw new UserAlreadyExistsException("User with email " + email + " already exists.");
    }

    try {
      User user = userService.create(userDto, email, passwordEncoder.encode(password));
      cognitoApiClientService.register(email, password, user.getId(),
          AccessLevel.getDefaultRole(), true);
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
      finalizeUserRegistration(user, email);
      return user.getId();
    } catch (Exception e) {
      log.error("Confirmation registration process was failed for email {}: {}",
          confirmationCodeDto.getEmail(), e.getMessage(), e);
      throw new UserRegistrationException("Confirmation registration process was failed.");
    }
  }

  /**
   * Finalizes the registration process for a user by adding an email contact and sending
   * greetings.
   *
   * @param user  The user whose registration is being finalized.
   * @param email The email address to be added as the user's contact.
   */
  public void finalizeUserRegistration(User user, String email) {
    Contact contact = Contact
        .builder()
        .type(ContactType.EMAIL)
        .value(email)
        .build();

    Optional.ofNullable(user.getContacts())
        .orElseGet(() -> {
          List<Contact> newList = new ArrayList<>();
          user.setContacts(newList);
          return newList;
        })
        .add(contact);
    sendGreetings(user, email);
  }

  /**
   * Resends the registration confirmation code to the user's email.
   *
   * @param resendConfirmCodeDto the DTO containing the user's email for which the confirmation code
   *                             should be resent.
   */
  public void resendRegistrationConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto) {
    try {
      cognitoApiClientService.resendRegistrationCode(resendConfirmCodeDto.getEmail());
    } catch (Exception e) {
      log.error("Resend confirmation code process was failed for email {}: {}",
          resendConfirmCodeDto.getEmail(), e.getMessage(), e);
      throw new UserRegistrationException("Resend confirmation code process was failed.");
    }
  }

  /**
   * Sends greetings to a user via email and adds a greeting notification.
   *
   * @param user  The user to whom greetings are sent.
   * @param email The email address of the user.
   */
  private void sendGreetings(User user, String email) {
    notificationService.addGreeting(user, email);
    emailService.sendGreetingsEmail(user, email);
  }
}
