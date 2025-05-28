package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_ACCOUNT_ACTIVE;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.exception.LogoutException;
import com.ratifire.devrate.security.exception.ProfileActivationException;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.LoginResponseWrapper;
import com.ratifire.devrate.security.model.dto.ConfirmActivationAccountDto;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.model.dto.ResendConfirmCodeDto;
import com.ratifire.devrate.security.model.enums.LoginStatus;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.security.util.CognitoUtil;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling authentication logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!local")
public class AuthenticationService {

  private final CognitoApiClientService cognitoApiClientService;
  private final UserService userService;
  private final EmailConfirmationCodeService emailConfirmationCodeService;
  private final EmailService emailService;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;
  private final PasswordEncoder passwordEncoder;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public LoginResponseDto login(LoginDto loginDto, HttpServletResponse response) {
    final String providedEmail = loginDto.getEmail();
    final String providedPassword = loginDto.getPassword();
    User user = userService.findByEmail(providedEmail);
    final String currentEncodedPassword = user.getPassword();

    if (!passwordEncoder.matches(providedPassword, currentEncodedPassword)) {
      throw new AuthenticationException(
          "Authentication process was failed due to invalid password.");
    }


    if (Boolean.FALSE.equals(user.getAccountActivated())) {
      return handleInactiveAccount(user);
    }
    LoginResponseWrapper responseWrapper = processLogin(response, user, providedPassword);
    return responseWrapper.loginResponse();

  }

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
    User activatedUser = activateAccountByConfirmationCode(dto.getActivationCode());

    if (activatedUser.getRegistrationSource().equals(RegistrationSourceType.FEDERATED_IDENTITY)) {
      accessToken = TokenUtil.extractAccessTokenFromRequest(request);
      idToken = TokenUtil.extractIdTokenFromRequest(request);
      refreshToken = TokenUtil.extractRefreshTokenFromRequest(request);

      if (StringUtils.isEmpty(idToken) && StringUtils.isEmpty(refreshToken)) {
        throw new ProfileActivationException(
            "Profile cannot be activated via federated identity flow due to missing tokens.");
      }

      updateCognitoIsAccountActiveAttribute(response, accessToken, refreshToken);
      responseWrapper = processLogin(response, activatedUser, dto.getPassword());
    } else {
      responseWrapper = processLogin(response, activatedUser, dto.getPassword());
      accessToken = responseWrapper.accessToken();
      refreshToken = responseWrapper.refreshToken();
      updateCognitoIsAccountActiveAttribute(response, accessToken, refreshToken);
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
    final long userId = user.getId();

    EmailConfirmationCode code = emailConfirmationCodeService.findByUserId(userId);
    emailConfirmationCodeService.deleteConfirmedCode(code.getId());

    String newCode = emailConfirmationCodeService.createConfirmationCode(userId);
    emailService.sendAccountActivationCodeEmail(user.getEmail(), newCode);
  }

  /**
   * Logout the currently authenticated user.
   *
   * @param request The HTTP servlet request.
   * @return A ResponseEntity indicating the success of the logout operation.
   */
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    try {
      String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
      cognitoApiClientService.logout(accessToken);
      refreshTokenCookieHelper.deleteRefreshTokenFromCookie(response);
      return "Logout process was successfully completed.";
    } catch (NotAuthorizedException e) {
      log.error("Access token has expired: {}", e.getMessage());
      throw new AuthTokenExpiredException("Access token has expired.");
    } catch (Exception e) {
      log.error("Logout process was failed: {}", e.getMessage());
      throw new LogoutException("Logout process was failed.");
    }
  }

  /**
   * Processes user login by authenticating credentials.
   *
   * @param response the HttpServletResponse to which authentication tokens and cookies will be
   *                 added
   * @param user     the User entity to authenticate
   * @param password the user's password
   * @return a LoginResponseDto containing the authentication status and user information
   * @throws AuthenticationException if authentication fails
   */
  public LoginResponseWrapper processLogin(HttpServletResponse response, User user, String
      password) {
    final String email = user.getEmail();
    try {
      AuthenticationResultType result = cognitoApiClientService.login(email, password);
      TokenUtil.setAuthTokensToHeaders(response, result.getAccessToken(), result.getIdToken());
      refreshTokenCookieHelper.setRefreshTokenToCookie(response, result.getRefreshToken());

      return new LoginResponseWrapper(
          result.getAccessToken(),
          result.getRefreshToken(),
          result.getIdToken(),
          buildLoginResponseDto(LoginStatus.AUTHENTICATED, userMapper.toDto(user))
      );

    } catch (Exception e) {
      log.error("Authentication process was failed for email {}: {}", email, e.getMessage());
      throw new AuthenticationException("Authentication process was failed.");
    }
  }

  /**
   * Handles the case when an account is inactive.
   *
   * @param user The User entity that is not yet activated.
   * @return a LoginResponseDto with status ACTIVATION_REQUIRED and no user information.
   */
  public LoginResponseDto handleInactiveAccount(User user) {
    String activationCode = emailConfirmationCodeService.createConfirmationCode(user.getId());
    emailService.sendAccountActivationCodeEmail(user.getEmail(), activationCode);
    return buildLoginResponseDto(LoginStatus.ACTIVATION_REQUIRED, null);
  }

  /**
   * Activates a user account using the provided email confirmation code.
   *
   * @param code the email confirmation code used for account activation
   * @return the updated User entity with activated status
   */
  public User activateAccountByConfirmationCode(String code) {
    EmailConfirmationCode codeEntity = emailConfirmationCodeService.findByCode(code);
    emailConfirmationCodeService.validateExpiration(codeEntity);
    emailConfirmationCodeService.deleteConfirmedCode(codeEntity.getId());

    User user = userService.findById(codeEntity.getUserId());
    user.setAccountActivated(true);
    userService.updateByEntity(user);

    return user;
  }

  private void updateCognitoIsAccountActiveAttribute(HttpServletResponse response,
      String accessToken, String refreshToken) {
    String username = TokenUtil.getSubjectFromAccessToken(accessToken);
    List<AttributeType> attributeToUpdate = List.of(
        CognitoUtil.createAttribute(ATTRIBUTE_IS_ACCOUNT_ACTIVE, Boolean.TRUE.toString()));
    cognitoApiClientService.updateCognitoUserAttributes(attributeToUpdate, username);
    refreshAuthTokens(response, refreshToken, username);
  }

  private void refreshAuthTokens(HttpServletResponse response, String refreshToken,
      String username) {
    AuthenticationResultType refreshedTokens =
        cognitoApiClientService.refreshAuthTokens(username, refreshToken);
    TokenUtil.setAuthTokensToHeaders(response, refreshedTokens.getAccessToken(),
        refreshedTokens.getIdToken());
  }

  private LoginResponseDto buildLoginResponseDto(LoginStatus status, UserDto user) {
    return LoginResponseDto.builder()
        .status(status)
        .userInfo(user)
        .build();
  }
}
