package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.AuthenticationException;
import com.ratifire.devrate.jwt.service.JwtService;
import com.ratifire.devrate.jwt.service.RefreshTokenService;
import com.ratifire.devrate.jwt.util.CookieHelper;
import com.ratifire.devrate.mapper.DataMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication logic.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserSecurityService userSecurityService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final CookieHelper cookieHelper;
  private final AuthenticationManager authenticationManager;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public UserDto login(LoginDto loginDto, HttpServletResponse response) {
    String login = loginDto.getEmail();
    String password = loginDto.getPassword();

    try {
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(login, password);
      authenticationManager.authenticate(authenticationToken);

      UserSecurity userSecurity = userSecurityService.findByEmail(login);
      String accessToken = jwtService.generateAccessToken(userSecurity);
      String refreshToken = jwtService.generateRefreshToken(userSecurity);
      refreshTokenService.save(userSecurity.getUser(), refreshToken);

      response.setHeader("Authorization", "Bearer " + accessToken);
      response.addCookie(cookieHelper.createRefreshTokenCookie(refreshToken));

      return userMapper.toDto(userSecurityService.findByEmail(login).getUser());
    } catch (DisabledException exception) {
      if (exception.getCause() instanceof DisabledException) {
        throw new DisabledException("User was not verified.");
      }
      throw new AuthenticationException("User was not authenticated.");
    }
  }

  /**
   * Logout the currently authenticated user.
   *
   * @param request The HTTP servlet request.
   * @return A ResponseEntity indicating the success of the logout operation.
   */
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
    try {
      String refreshToken = jwtService.extractRefreshTokenFromCookies(request);
      refreshTokenService.delete(refreshToken);
      cookieHelper.deleteRefreshTokenCookie(response);
      request.logout();
      return ResponseEntity.ok("Successfully logged out.");
    } catch (ServletException e) {
      throw new AuthenticationException("Error while logout.");
    }
  }
}
