package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user authentication.
 */
@Service
@RequiredArgsConstructor
public class LoginService {

  private final UserSecurityService userSecurityService;
  private final AuthenticationManager authenticationManager;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginDto The data transfer object containing the user's login information.
   * @return A UserDto object representing the authenticated user.
   */
  public UserDto authenticate(LoginDto loginDto) {
    String login = loginDto.getEmail();
    String password = loginDto.getPassword();

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(login, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    return userMapper.toDto(userSecurityService.findByEmail(login).getUser());
  }
}
