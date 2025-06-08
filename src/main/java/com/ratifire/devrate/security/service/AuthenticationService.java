package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.exception.LogoutException;
import com.ratifire.devrate.security.facade.AccountActivationEmailFacade;
import com.ratifire.devrate.security.factory.LoginResponseFactory;
import com.ratifire.devrate.security.helper.AuthTokenHelper;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.LoginResponseWrapper;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;
  private final AuthTokenHelper authTokenHelper;
  private final AccountActivationEmailFacade accountActivationEmailFacade;
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
      accountActivationEmailFacade.sendNewActivationCode(user);
      return LoginResponseFactory.activationRequired();
    }

    LoginResponseWrapper responseWrapper = processLogin(response, user, providedPassword);
    return responseWrapper.loginResponse();

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

      authTokenHelper.setAuthTokensToResponse(
          response, null, result.getAccessToken(), result.getIdToken(), result.getRefreshToken(),
          false, true);

      return new LoginResponseWrapper(
          result.getAccessToken(),
          result.getRefreshToken(),
          result.getIdToken(),
          LoginResponseFactory.success(userMapper.toDto(user))
      );

    } catch (Exception e) {
      log.error("Authentication process was failed for email {}: {}", email, e.getMessage());
      throw new AuthenticationException("Authentication process was failed.");
    }
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

}