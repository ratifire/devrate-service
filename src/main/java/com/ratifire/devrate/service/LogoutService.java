package com.ratifire.devrate.service;

import com.ratifire.devrate.exception.AuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for user authentication.
 */
@Service
@RequiredArgsConstructor
public class LogoutService {

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
