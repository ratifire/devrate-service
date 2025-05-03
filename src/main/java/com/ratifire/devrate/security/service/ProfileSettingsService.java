package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL_VERIFIED;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_ACCOUNT_ACTIVE;
import static com.ratifire.devrate.security.util.CognitoUtil.createAttribute;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.ratifire.devrate.dto.InterviewRequestShortDto;
import com.ratifire.devrate.dto.InterviewShortDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.security.exception.EmailChangeException;
import com.ratifire.devrate.security.exception.PasswordChangeException;
import com.ratifire.devrate.security.exception.ProfileDeactivationConflictException;
import com.ratifire.devrate.security.exception.UserAlreadyExistsException;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.security.model.dto.EmailChangeDto;
import com.ratifire.devrate.security.model.dto.PasswordChangeDto;
import com.ratifire.devrate.security.model.dto.ProfileDeactivationDto;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.security.util.CognitoUtil;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.MasteryService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import com.ratifire.devrate.service.interview.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for managing user profile settings.
 */
@Service
@RequiredArgsConstructor
public class ProfileSettingsService {

  private final UserContextProvider userContextProvider;
  private final CognitoApiClientService cognitoClient;
  private final UserService userService;
  private final AuthenticationService authenticationService;
  private final RefreshTokenService refreshTokenService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;
  private final PasswordEncoder passwordEncoder;
  private final MasteryService masteryService;

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
    final String providedEmail = emailChangeDto.getCurrentEmail();
    final String newEmail = emailChangeDto.getNewEmail();
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    User currentUser = userService.findById(currentUserId);

    if (userService.existsByEmail(newEmail)) {
      throw new UserAlreadyExistsException("User with email " + newEmail + " already exists.");
    }

    validateCurrentEmailBeforeProcessingChange(currentUser, providedEmail);

    List<AttributeType> attributeToUpdate = List.of(
        createAttribute(ATTRIBUTE_EMAIL, newEmail),
        createAttribute(ATTRIBUTE_EMAIL_VERIFIED, Boolean.TRUE.toString()));
    cognitoClient.updateCognitoUserAttributes(attributeToUpdate, providedEmail);

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

  /**
   * Forces deactivation of the currently authenticated user's profile if there are no blocking
   * interview requests or upcoming interviews, and logs the user out of the system.
   *
   * @param request  the HttpServletRequest containing authentication details
   * @param response the HttpServletResponse used to clear authentication tokens
   * @throws ProfileDeactivationConflictException if there are pending interview requests or
   *                                              upcoming interviews
   */
  public void forceUserProfileDeactivation(HttpServletRequest request,
      HttpServletResponse response) {
    final long currentUserId = userContextProvider.getAuthenticatedUserId();
    final String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
    final String username = TokenUtil.getSubjectFromAccessToken(accessToken);

    deactivateAccountIfNoBlockingData(currentUserId);

    List<AttributeType> attributeToUpdate = List.of(
        CognitoUtil.createAttribute(ATTRIBUTE_IS_ACCOUNT_ACTIVE, Boolean.FALSE.toString()));
    cognitoClient.updateCognitoUserAttributes(attributeToUpdate, username);

    authenticationService.logout(request, response);
  }

  /**
   * Deactivates the user profile if there are no pending interview requests with future time slots
   * or upcoming interviews.
   *
   * @param userId the ID of the user to deactivate
   * @throws ProfileDeactivationConflictException if there are blocking interview requests or
   *                                              upcoming interviews
   */
  public void deactivateAccountIfNoBlockingData(long userId) {
    List<InterviewRequest> interviewRequestsWithFutureTimeSlots =
        interviewRequestService.findAllWithFutureTimeSlots(userId);
    List<Interview> upcomingInterviews =
        interviewService.getUpcomingInterviews(userId, ZonedDateTime.now());

    if (!CollectionUtils.isEmpty(interviewRequestsWithFutureTimeSlots)
        && !CollectionUtils.isEmpty(upcomingInterviews)) {
      ProfileDeactivationDto dto = ProfileDeactivationDto.builder()
          .interviewRequestsToDelete(
              mapToInterviewRequestShortDtos(interviewRequestsWithFutureTimeSlots))
          .interviewsToDelete(mapToInterviewShortDtos(upcomingInterviews))
          .build();
      throw new ProfileDeactivationConflictException(dto);
    }

    User user = userService.findById(userId);
    user.setAccountActivated(false);
    userService.updateByEntity(user);
  }

  private void validateCurrentEmailBeforeProcessingChange(User currentUser,
      String emailToValidate) {

    if (StringUtils.isEmpty(emailToValidate)
        || !currentUser.getEmail().equals(emailToValidate)) {
      throw new EmailChangeException(
          "Email of current authenticated user does not match the provided email.");
    }

    if (RegistrationSourceType.FEDERATED_IDENTITY.equals(currentUser.getRegistrationSource())) {
      throw new EmailChangeException("Email change is not allowed for SSO accounts.");
    }
  }

  private void validatePasswordBeforeProcessingChange(User user, String passwordToValidate) {

    if (StringUtils.isEmpty(passwordToValidate)
        || !passwordEncoder.matches(passwordToValidate, user.getPassword())) {
      throw new PasswordChangeException(
          "Password of current authenticated user does not match the provided password.");
    }

    if (RegistrationSourceType.FEDERATED_IDENTITY.equals(user.getRegistrationSource())) {
      throw new PasswordChangeException("Password change is not allowed for SSO accounts.");
    }
  }

  private List<InterviewRequestShortDto> mapToInterviewRequestShortDtos(
      List<InterviewRequest> requests) {
    return requests.stream()
        .map(request -> InterviewRequestShortDto.builder()
            .id(request.getId())
            .role(request.getRole())
            .specializationName(request.getMastery().getSpecialization().getName())
            .build())
        .toList();
  }

  private List<InterviewShortDto> mapToInterviewShortDtos(List<Interview> interviews) {
    List<Long> masteryIds = interviews.stream()
        .map(Interview::getMasteryId)
        .distinct()
        .toList();
    List<Mastery> masteries = masteryService.findByIds(masteryIds);
    Map<Long, Mastery> masteryMap = masteries.stream()
        .collect(Collectors.toMap(Mastery::getId, Function.identity()));
    return interviews.stream()
        .map(interview -> {
          Mastery mastery = masteryMap.get(interview.getMasteryId());
          return InterviewShortDto.builder()
              .id(interview.getId())
              .masteryLevel(mastery.getLevel())
              .specializationName(mastery.getSpecialization().getName())
              .startTime(interview.getStartTime())
              .role(interview.getRole())
              .build();
        })
        .toList();
  }

}