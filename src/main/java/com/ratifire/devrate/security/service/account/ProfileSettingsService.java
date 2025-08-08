package com.ratifire.devrate.security.service.account;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL_VERIFIED;
import static com.ratifire.devrate.security.util.CognitoUtil.createAttribute;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.exception.EmailChangeException;
import com.ratifire.devrate.security.exception.PasswordChangeException;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.security.service.CognitoApiClientService;
import com.ratifire.devrate.security.service.CognitoUserSyncService;
import com.ratifire.devrate.security.service.RefreshTokenService;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing user profile settings.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class ProfileSettingsService {

  private final UserContextProvider userContextProvider;
  private final CognitoApiClientService cognitoClient;
  private final UserService userService;
  private final RefreshTokenService refreshTokenService;
  private final CognitoUserSyncService cognitoUserSyncService;
  private final PasswordEncoder passwordEncoder;

  /**
   * Changes the authenticated user's email address.
   *
   * @param request        the HttpServletRequest containing authentication details
   * @param response       the HttpServletResponse used to set updated tokens
   * @param emailChangeDto the DTO containing current and new email addresses
   * @throws UserAlreadyExistsException if the new email is already in use
   * @throws EmailChangeException       if the current email is invalid or email change is not
   *                                    allowed
   */
  public void changeEmail(HttpServletRequest request, HttpServletResponse response,
      EmailChangeDto emailChangeDto) {
    final String providedEmail = emailChangeDto.getCurrentEmail().toLowerCase();
    final String newEmail = emailChangeDto.getNewEmail().toLowerCase();
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User currentUser = userService.findById(currentUserId);

    if (userService.existsByEmail(newEmail)) {
      throw new UserAlreadyExistsException("User with email " + newEmail + " already exists.");
    }

    validateCurrentEmailBeforeProcessingChange(currentUser, providedEmail);

    cognitoUserSyncService.synchronizeAttributeWithCognitoForSingleUser(providedEmail, List.of(
        createAttribute(ATTRIBUTE_EMAIL, newEmail),
        createAttribute(ATTRIBUTE_EMAIL_VERIFIED, Boolean.TRUE.toString())));

    currentUser.setEmail(newEmail);
    userService.updateByEntity(currentUser);

    refreshTokenService.refreshAuthTokens(request, response);
  }

  /**
   * Changes the authenticated user's password.
   *
   * @param request           the HttpServletRequest containing authentication details
   * @param passwordChangeDto the DTO containing the current and new passwords
   * @throws PasswordChangeException if the current password is invalid or password change is not
   *                                 allowed
   */
  public void changePassword(HttpServletRequest request, PasswordChangeDto passwordChangeDto) {
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    final String currentPassword = passwordChangeDto.getCurrentPassword();
    final String newPassword = passwordChangeDto.getNewPassword();
    final String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
    User currentUser = userService.findById(currentUserId);

    validatePasswordBeforeProcessingChange(currentUser, currentPassword);
    cognitoClient.changePassword(accessToken, currentPassword, newPassword);

    currentUser.setPassword(passwordEncoder.encode(newPassword));
    userService.updateByEntity(currentUser);
  }

  /**
   * Updates the email subscription flag for the currently authenticated user.
   *
   * @param isEnabled {@code true} to enable email subscription, {@code false} to disable it
   */
  public void updateEmailSubscriptionFlag(boolean isEnabled) {
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User user = userService.findById(currentUserId);
    user.setSubscribed(isEnabled);
    userService.updateByEntity(user);
  }

  /**
   * Updates the account language for the currently authenticated user.
   *
   * @param accountLanguage the new account language to set
   */
  public void updateAccountLanguage(AccountLanguage accountLanguage) {
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User user = userService.findById(currentUserId);
    user.setAccountLanguage(accountLanguage);
    userService.updateByEntity(user);
  }

  private void validateCurrentEmailBeforeProcessingChange(User currentUser,
      String emailToValidate) {

    if (StringUtils.isEmpty(emailToValidate)
        || !currentUser.getEmail().equals(emailToValidate)) {
      throw new EmailChangeException(
          "Email of current authenticated user does not match the provided email: "
              + emailToValidate);
    }

    if (RegistrationSourceType.FEDERATED_IDENTITY.equals(currentUser.getRegistrationSource())) {
      throw new EmailChangeException(
          "Email change is not allowed for SSO accounts. User ID: " + currentUser.getId());
    }
  }

  private void validatePasswordBeforeProcessingChange(User user, String passwordToValidate) {

    if (StringUtils.isEmpty(passwordToValidate)
        || !passwordEncoder.matches(passwordToValidate, user.getPassword())) {
      throw new PasswordChangeException(
          "Password of current authenticated user, ID: " + user.getId()
              + " does not match the provided password.");
    }

    if (RegistrationSourceType.FEDERATED_IDENTITY.equals(user.getRegistrationSource())) {
      throw new PasswordChangeException(
          "Password change is not allowed for SSO accounts. User ID: " + user.getId());
    }
  }
}