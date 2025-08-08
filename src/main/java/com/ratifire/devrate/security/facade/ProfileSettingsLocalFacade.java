package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.exception.EmailChangeException;
import com.ratifire.devrate.security.exception.PasswordChangeException;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Local implementation facade class for profile settings.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("local")
public class ProfileSettingsLocalFacade implements ProfileSettingsFacade {

  private final UserContextProvider userContextProvider;
  private final UserService userService;

  @Override
  public void changeEmail(HttpServletRequest request, HttpServletResponse response,
      EmailChangeDto emailChangeDto) {
    final String newEmail = emailChangeDto.getNewEmail().toLowerCase();
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User currentUser = userService.findById(currentUserId);

    if (userService.existsByEmail(newEmail)) {
      throw new UserAlreadyExistsException("User with email " + newEmail + " already exists.");
    }

    validateCurrentEmailBeforeProcessingChange(currentUser, emailChangeDto.getCurrentEmail());

    currentUser.setEmail(newEmail);
    userService.updateByEntity(currentUser);
  }

  @Override
  public void changePassword(HttpServletRequest request, PasswordChangeDto passwordChangeDto) {
    User currentUser = userService.findById(userContextProvider.getAuthenticatedUserId());

    validatePasswordBeforeProcessingChange(currentUser, passwordChangeDto.getCurrentPassword());

    currentUser.setPassword(new BCryptPasswordEncoder().encode(passwordChangeDto.getNewPassword()));
    userService.updateByEntity(currentUser);
  }

  @Override
  public void updateAccountLanguage(AccountLanguage language) {
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User user = userService.findById(currentUserId);
    user.setAccountLanguage(language);
    userService.updateByEntity(user);
  }

  @Override
  public void updateEmailSubscriptionFlag(boolean isEnabled) {
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User user = userService.findById(currentUserId);
    user.setSubscribed(isEnabled);
    userService.updateByEntity(user);
  }

  private void validateCurrentEmailBeforeProcessingChange(User currentUser,
      String emailToValidate) {

    if (StringUtils.isEmpty(emailToValidate)
        || !currentUser.getEmail().equals(emailToValidate.toLowerCase())) {
      throw new EmailChangeException(
          "Email of current authenticated user does not match the provided email: "
              + emailToValidate);
    }

    if (!RegistrationSourceType.LOCAL.equals(currentUser.getRegistrationSource())) {
      throw new EmailChangeException(
          "Email change is not allowed for non-local accounts. User ID: " + currentUser.getId());
    }
  }

  private void validatePasswordBeforeProcessingChange(User user, String passwordToValidate) {
    new BCryptPasswordEncoder().matches(passwordToValidate, user.getPassword());
    if (StringUtils.isEmpty(passwordToValidate)
        || !new BCryptPasswordEncoder().matches(passwordToValidate, user.getPassword())) {
      throw new PasswordChangeException(
          "Password of current authenticated user, ID: " + user.getId()
              + " does not match the provided password.");
    }

    if (!RegistrationSourceType.LOCAL.equals(user.getRegistrationSource())) {
      throw new PasswordChangeException(
          "Password change is not allowed for non-local accounts. User ID: " + user.getId());
    }
  }
}