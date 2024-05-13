package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.AuthenticationException;
import com.ratifire.devrate.mapper.DataMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication logic.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserSecurityService userSecurityService;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public UserDto login(LoginDto loginDto, HttpServletRequest request) {
    String login = loginDto.getEmail();
    String password = loginDto.getPassword();

    try {
      request.login(login, password);
      return userMapper.toDto(userSecurityService.findByEmail(login).getUser());
    } catch (ServletException exception) {
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
  public ResponseEntity<String> logout(HttpServletRequest request) {
    try {
      request.logout();
      return ResponseEntity.ok().body("Successfully logged out.");
    } catch (ServletException e) {
      throw new AuthenticationException("Error while logout.");
    }
  }
}
