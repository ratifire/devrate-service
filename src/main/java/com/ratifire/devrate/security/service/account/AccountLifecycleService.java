package com.ratifire.devrate.security.service.account;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_ACCOUNT_ACTIVE;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.dto.projection.InterviewIdProjection;
import com.ratifire.devrate.dto.projection.InterviewRequestTimeSlotProjection;
import com.ratifire.devrate.dto.record.InterviewRequestWithFutureSlotsRecord;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.exception.ProfileActivationException;
import com.ratifire.devrate.security.facade.AccountActivationEmailFacade;
import com.ratifire.devrate.security.factory.LoginResponseFactory;
import com.ratifire.devrate.security.helper.AuthTokenHelper;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.security.model.LoginResponseWrapper;
import com.ratifire.devrate.security.model.dto.ConfirmActivationAccountDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.security.service.AuthenticationService;
import com.ratifire.devrate.security.service.CognitoUserSyncService;
import com.ratifire.devrate.security.service.EmailConfirmationCodeService;
import com.ratifire.devrate.security.util.CognitoUtil;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.interview.InterviewRequestService;
import com.ratifire.devrate.service.interview.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for managing user account activation and deactivation flows.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class AccountLifecycleService {

  private final AuthenticationService authenticationService;
  private final CognitoUserSyncService cognitoUserSyncService;
  private final UserService userService;
  private final InterviewRequestService interviewRequestService;
  private final InterviewService interviewService;
  private final EmailConfirmationCodeService emailConfirmationCodeService;
  private final AuthTokenHelper authTokenHelper;
  private final UserContextProvider userContextProvider;
  private final AccountActivationEmailFacade accountActivationEmailFacade;

  /**
   * Confirms user account activation using the provided activation code, handling both standard and
   * federated identity flows.
   *
   * @param dto      the DTO containing the activation code and password
   * @param response the HTTP response to which authentication tokens and cookies will be added
   * @param request  the HTTP request containing potential authentication tokens
   * @return a LoginResponseDto with authentication status and user information
   */
  @Transactional
  public LoginResponseDto confirmAccountActivation(ConfirmActivationAccountDto dto,
      HttpServletResponse response, HttpServletRequest request) {
    String accessToken;
    String idToken;
    String refreshToken;
    LoginResponseWrapper responseWrapper;
    User activatedUser = processAccountActivationByConfirmationCode(dto.getActivationCode());
    List<AttributeType> attributes = List.of(
        CognitoUtil.createAttribute(ATTRIBUTE_IS_ACCOUNT_ACTIVE, Boolean.TRUE.toString()));

    if (activatedUser.getRegistrationSource().equals(RegistrationSourceType.FEDERATED_IDENTITY)) {
      accessToken = TokenUtil.extractAccessTokenFromRequest(request);
      idToken = TokenUtil.extractIdTokenFromRequest(request);
      refreshToken = TokenUtil.extractRefreshTokenFromRequest(request);

      if (StringUtils.isEmpty(idToken) && StringUtils.isEmpty(refreshToken)) {
        throw new ProfileActivationException(
            "Profile cannot be activated via federated identity flow due to missing tokens.");
      }

      cognitoUserSyncService.synchronizeAttributeWithCognitoForSingleUser(
          TokenUtil.getSubjectFromAccessToken(accessToken), attributes);
      responseWrapper = authenticationService.processLogin(response, activatedUser,
          dto.getPassword());
    } else {
      responseWrapper = authenticationService.processLogin(response, activatedUser,
          dto.getPassword());
      accessToken = responseWrapper.accessToken();
      refreshToken = responseWrapper.refreshToken();
      String subject = TokenUtil.getSubjectFromAccessToken(accessToken);
      cognitoUserSyncService.synchronizeAttributeWithCognitoForSingleUser(subject, attributes);
      authTokenHelper.setAuthTokensToResponse(
          response, subject, null, null, refreshToken, true, false);
    }

    return responseWrapper.loginResponse();
  }

  /**
   * Resends a new account activation confirmation code to the user's email address.
   *
   * @param resendConfirmCodeDto DTO containing the user's email address.
   */
  public void resendActivationAccountConfirmCode(ResendConfirmCodeDto resendConfirmCodeDto) {
    final User user = userService.findByEmail(resendConfirmCodeDto.getEmail());
    accountActivationEmailFacade.resendActivationCode(user);
  }


  /**
   * Handles the flow for inactive user accounts by optionally synchronizing the account status with
   * Cognito.
   *
   * @param response                the HTTP response to update with authentication tokens
   * @param internalUser            the internal user entity
   * @param username                the Cognito username
   * @param refreshToken            the refresh token for authentication
   * @param isSyncWithCognitoNeeded whether to synchronize the inactive status with Cognito
   * @return a LoginResponseDto indicating that account activation is required
   */
  public LoginResponseDto handleInactiveAccountFlow(
      HttpServletResponse response,
      User internalUser,
      String username,
      String refreshToken,
      boolean isSyncWithCognitoNeeded) {

    if (isSyncWithCognitoNeeded) {
      cognitoUserSyncService.synchronizeAttributeWithCognitoForSingleUser(username,
          List.of(
              CognitoUtil.createAttribute(ATTRIBUTE_IS_ACCOUNT_ACTIVE, Boolean.FALSE.toString())));

      authTokenHelper.setAuthTokensToResponse(
          response, username, null, null, refreshToken, true, true);

    }
    accountActivationEmailFacade.sendNewActivationCode(internalUser);
    return LoginResponseFactory.activationRequired();
  }

  /**
   * Forces deactivation of the currently authenticated user's profile if there are no blocking
   * interview requests or upcoming interviews, and logs the user out of the system.
   *
   * @param request  the HttpServletRequest containing authentication details
   * @param response the HttpServletResponse used to clear authentication tokens
   */
  @Transactional
  public void processAccountDeactivation(HttpServletRequest request,
      HttpServletResponse response) {
    final String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
    final String email = TokenUtil.getEmailFromIdToken(accessToken);
    final long currentUserId = userContextProvider.getAuthenticatedUserId();

    deactivateAccountIfNoBlockingFutureActivity(currentUserId);

    cognitoUserSyncService.synchronizeAttributeWithCognitoForAllLinkedUser(email, List.of(
        CognitoUtil.createAttribute(ATTRIBUTE_IS_ACCOUNT_ACTIVE, Boolean.FALSE.toString())));
    authenticationService.logout(request, response);
  }

  /**
   * Deactivates the user profile if there are no pending interview requests with future time slots
   * or upcoming interviews.
   *
   * @param userId the ID of the user to deactivate
   */
  private void deactivateAccountIfNoBlockingFutureActivity(long userId) {
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
          request ->
              interviewRequestService.deleteTimeSlots(request.id(), request.futureTimeSlots())
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
  }

  /**
   * Activates a user account using the provided email confirmation code.
   *
   * @param code the email confirmation code used for account activation
   * @return the updated User entity with activated status
   */
  private User processAccountActivationByConfirmationCode(String code) {
    EmailConfirmationCode codeEntity = emailConfirmationCodeService.findByCode(code);
    emailConfirmationCodeService.validateExpiration(codeEntity);
    emailConfirmationCodeService.deleteConfirmedCode(codeEntity.getId());

    User user = userService.findById(codeEntity.getUserId());
    user.setAccountActivated(true);
    userService.updateByEntity(user);

    return user;
  }

}