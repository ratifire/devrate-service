package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.AuthenticationException;
import com.ratifire.devrate.mapper.DataMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user authentication.
 */
@Service
@RequiredArgsConstructor
public class LoginService {

  private final UserSecurityService userSecurityService;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public UserDto authenticate(LoginDto loginDto, HttpServletRequest request) {
    String login = loginDto.getEmail();
    String password = loginDto.getPassword();

    try {
      request.login(login, password);
      return userMapper.toDto(userSecurityService.findByEmail(login).getUser());
    } catch (ServletException exception) {
      if (exception.getCause().getClass() == DisabledException.class) {
        throw new DisabledException("User was not verified.");
      }
      throw new AuthenticationException("User was not authenticated.");
    }
  }
}
