package com.ratifire.devrate.security.configuration.handler;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_AUTHENTICATION_ERROR;

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

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) {
    String error = request.getAttribute(ATTRIBUTE_AUTHENTICATION_ERROR).toString();
    int responseStatus = determineResponseStatus(error);
    response.setStatus(responseStatus);
  }

  private int determineResponseStatus(String error) {
    if (AuthenticationError.AUTH_TOKEN_EXPIRED.name().equals(error)) {
      return AuthenticationError.AUTH_TOKEN_EXPIRED.getHttpStatus();
    }
    return AuthenticationError.UNAUTHORIZED.getHttpStatus();
  }
}