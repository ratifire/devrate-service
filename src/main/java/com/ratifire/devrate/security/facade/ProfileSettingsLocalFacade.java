package com.ratifire.devrate.security.facade;

import com.ratifire.devrate.dto.projection.InterviewIdProjection;
import com.ratifire.devrate.dto.projection.InterviewRequestTimeSlotProjection;
import com.ratifire.devrate.dto.record.InterviewRequestWithFutureSlotsRecord;
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
import com.ratifire.devrate.service.MasteryService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import com.ratifire.devrate.service.interview.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
  private final MasteryService masteryService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;

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
  public void forceUserProfileDeactivation(HttpServletRequest request,
      HttpServletResponse response) {
    final long userId = userContextProvider.getAuthenticatedUserId();

    List<InterviewRequestWithFutureSlotsRecord> requestWithFutureTimeSlotsAggregation =
        interviewRequestService.findAllInterviewRequestWithFutureTimeSlots(
                ZonedDateTime.now()).stream()
            .collect(Collectors.groupingBy(
                InterviewRequestTimeSlotProjection::getId,
                Collectors.mapping(InterviewRequestTimeSlotProjection::getDateTime,
                    Collectors.toList())
            ))
            .entrySet().stream()
            .map(entry ->
                new InterviewRequestWithFutureSlotsRecord(entry.getKey(), entry.getValue()))
            .toList();

    if (!CollectionUtils.isEmpty(requestWithFutureTimeSlotsAggregation)) {
      requestWithFutureTimeSlotsAggregation.forEach(
          interviewRequest ->
              interviewRequestService.deleteTimeSlots(interviewRequest.id(),
                  interviewRequest.futureTimeSlots())
      );
    }

    List<InterviewIdProjection> upcomingInterviewIds =
        interviewService.getUpcomingInterviewIds(userId, ZonedDateTime.now());

    if (!CollectionUtils.isEmpty(upcomingInterviewIds)) {
      upcomingInterviewIds.forEach(
          interview -> interviewService.deleteRejected(interview.getId())
      );
    }

    User user = userService.findById(userId);
    user.setAccountActivated(false);
    userService.updateByEntity(user);
    refreshTokenCookieHelper.deleteRefreshTokenFromCookie(response);
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
        || !currentUser.getEmail().equals(emailToValidate)) {
      throw new EmailChangeException(
          "Email of current authenticated user does not match the provided email.");
    }

    if (!RegistrationSourceType.LOCAL.equals(currentUser.getRegistrationSource())) {
      throw new EmailChangeException("Email change is not allowed for non-local accounts.");
    }
  }

  private void validatePasswordBeforeProcessingChange(User user, String passwordToValidate) {
    new BCryptPasswordEncoder().matches(passwordToValidate, user.getPassword());
    if (StringUtils.isEmpty(passwordToValidate)
        || !new BCryptPasswordEncoder().matches(passwordToValidate, user.getPassword())) {
      throw new PasswordChangeException(
          "Password of current authenticated user does not match the provided password.");
    }

    if (!RegistrationSourceType.LOCAL.equals(user.getRegistrationSource())) {
      throw new PasswordChangeException("Password change is not allowed for non-local accounts.");
    }
  }
}