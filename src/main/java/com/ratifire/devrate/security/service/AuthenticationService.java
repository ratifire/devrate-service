package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthTokenExpiredException;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.exception.LogoutException;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public UserDto login(LoginDto loginDto, HttpServletResponse response) {
    String email = loginDto.getEmail();
    String password = loginDto.getPassword();
    try {
      AuthenticationResultType result = cognitoApiClientService.login(email, password);
      TokenUtil.setAuthTokensToHeaders(response, result.getAccessToken(), result.getIdToken());
      refreshTokenCookieHelper.setRefreshTokenToCookie(response, result.getRefreshToken());
      User user = userService.findByEmail(email);
      return userMapper.toDto(user);

    } catch (Exception e) {
      log.error("Authentication process was failed for email {}: {}", email, e.getMessage(), e);
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
      log.error("Access token has expired: {}", e.getMessage(), e);
      throw new AuthTokenExpiredException("Access token has expired.");
    } catch (Exception e) {
      log.error("Logout process was failed: {}", e.getMessage(), e);
      throw new LogoutException("Logout process was failed.");
    }
  }
}
