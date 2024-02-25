package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication.
 */
@RestController
@RequestMapping("/auth/signin")
@RequiredArgsConstructor
public class LoginController {

  private final AuthenticationManager authenticationManager;

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * <p>@param loginDto The DTO containing the user's login information
   *
   * <p>@return ResponseEntity indicating the status of the authentication attempt
   */
  @PostMapping
  public LoginDto authenticateUser(@RequestBody LoginDto loginDto) {
    String login = loginDto.getEmail();
    String password = loginDto.getPassword();

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(login, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    return loginDto;
  }
}