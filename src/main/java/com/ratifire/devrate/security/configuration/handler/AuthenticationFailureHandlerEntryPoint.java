package com.ratifire.devrate.security.configuration.handler;

import com.ratifire.devrate.security.model.enums.AuthenticationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of AuthenticationEntryPoint to handle authentication failures.
 */
@Component
public class AuthenticationFailureHandlerEntryPoint implements AuthenticationEntryPoint {

  private static final String AUTHENTICATION_ERROR_ATTRIBUTE = "authentication_error";

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) {
//    String error = request.getAttribute(AUTHENTICATION_ERROR_ATTRIBUTE).toString();
//    int responseStatus = determineResponseStatus(error);
//    response.setStatus(responseStatus);
  }

  private int determineResponseStatus(String error) {
    if (AuthenticationError.TOKEN_EXPIRED.name().equals(error)) {
      return AuthenticationError.TOKEN_EXPIRED.getHttpStatus();
    }
    return AuthenticationError.UNAUTHORIZED.getHttpStatus();
  }
}