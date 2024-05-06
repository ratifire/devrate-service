package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication.
 */
@RestController
@RequestMapping("/auth/logout")
@RequiredArgsConstructor
public class LogoutController {

  private final LogoutService logoutService;

  /**
   * Endpoint for user logout.
   *
   * @param request The HttpServletRequest object representing the HTTP request.
   * @return ResponseEntity representing the result of the logout operation.
   */
  @PostMapping
  public ResponseEntity<String> logout(HttpServletRequest request) {
    return logoutService.logout(request);
  }
}
