package com.ratifire.devrate.security.controller;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.security.service.CognitoOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OAuthController {

  private final CognitoOAuthService cognitoOAuthService;

  /**
   * Redirects the user to the specified provider for authentication.
   *
   * @param response     the HTTP response object used for redirection
   * @param session      the current session to store the unique state
   * @param providerName the name of the identity provider (e.g., "Google", "LinkedIn")
   * @throws IOException if an error occurs during redirection
   */
  @GetMapping("/{providerName}/login")
  public void redirectToProviderLogin(HttpServletResponse response, HttpSession session,
      @PathVariable String providerName) throws IOException {
    String authUrl = cognitoOAuthService.generateAuthorizationUrl(session, providerName);
    response.sendRedirect(authUrl);
  }

  /**
   * Handles the callback from the identity provider after authentication.
   *
   * @param code  the authorization code received from the provider
   * @param state the unique state value to validate the request
   * @return a map containing the tokens
   */
  @GetMapping("/callback")
  public UserDto handleProviderCallback(@RequestParam("code") String code,
      @RequestParam("state") String state, HttpServletResponse response) {
    return cognitoOAuthService.exchangeAuthorizationCodeForTokens(code, response);
  }
}