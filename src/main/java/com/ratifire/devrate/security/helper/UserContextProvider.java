package com.ratifire.devrate.security.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility for accessing the authenticated user's context.
 */
@Component
public class UserContextProvider {

  /**
   * Retrieves the authenticated user's ID from the security context.
   *
   * @return the authenticated user's ID as a {@code long}.
   * @throws IllegalStateException if no authentication is found or if the user ID cannot be
   *                               extracted.
   */
  public long getAuthenticatedUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found");
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof Long userId) {
      return userId;
    }

    throw new IllegalStateException("Unable to extract user ID from authentication principal");
  }
}