package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.exception.EmailChangeException;
import com.ratifire.devrate.security.exception.PasswordChangeException;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.security.service.ProfileSettingsService;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final ProfileSettingsService profileSettingsService;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void changeEmail(HttpServletRequest request, HttpServletResponse response,
      EmailChangeDto emailChangeDto) {
    final String newEmail = emailChangeDto.getNewEmail();
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

    currentUser.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
    userService.updateByEntity(currentUser);
  }

  @Override
  public void updateAccountLanguage(AccountLanguage language) {
    profileSettingsService.updateAccountLanguage(language);
  }

  @Override
  public void forceUserProfileDeactivation(HttpServletRequest request,
      HttpServletResponse response) {
    long currentUserId = userContextProvider.getAuthenticatedUserId();
    profileSettingsService.deactivateAccountIfNoBlockingData(currentUserId);
    refreshTokenCookieHelper.deleteRefreshTokenFromCookie(response);
  }

  @Override
  public void updateEmailSubscriptionFlag(boolean isEnabled) {
    profileSettingsService.updateEmailSubscriptionFlag(isEnabled);
  }

  private void validateCurrentEmailBeforeProcessingChange(User currentUser,
      String emailToValidate) {

    if (StringUtils.isEmpty(emailToValidate)
        || !currentUser.getEmail().equals(emailToValidate)) {
      throw new EmailChangeException(
          "Email of current authenticated user does not match the provided email.");
    }

    if (!RegistrationSourceType.LOCAL.equals(currentUser.getRegistrationSource())) {
      throw new EmailChangeException("Email change is not allowed for non-local accounts.");
    }
  }

  private void validatePasswordBeforeProcessingChange(User user, String passwordToValidate) {
    if (StringUtils.isEmpty(passwordToValidate)
        || !passwordEncoder.matches(passwordToValidate, user.getPassword())) {
      throw new PasswordChangeException(
          "Password of current authenticated user does not match the provided password.");
    }

    if (!RegistrationSourceType.LOCAL.equals(user.getRegistrationSource())) {
      throw new PasswordChangeException("Password change is not allowed for non-local accounts.");
    }
  }
}