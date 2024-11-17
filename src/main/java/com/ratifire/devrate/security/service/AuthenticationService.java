package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.dto.LoginDto;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication logic.
 */
@Service
@RequiredArgsConstructor
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
      throw new AuthenticationException("User authentication was failed.");
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
      return "Successfully logged out.";
    } catch (Exception e) {
      throw new AuthenticationException("User authentication was failed.");
    }
  }
}