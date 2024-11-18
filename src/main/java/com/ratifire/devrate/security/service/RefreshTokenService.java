package com.ratifire.devrate.security.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.ratifire.devrate.security.exception.RefreshTokenException;
import com.ratifire.devrate.security.exception.RefreshTokenExpiredException;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling the refresh token process.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
  private final CognitoApiClientService cognitoApiClientService;

  /**
   * Refreshes the authentication tokens using the provided refresh token.
   *
   * @param request  the HttpServletRequest containing the refresh token.
   * @param response the HttpServletResponse used to set the updated tokens and refresh token
   *                 cookie.
   */
  public void refreshAuthTokens(HttpServletRequest request, HttpServletResponse response) {
    try {
      String token = TokenUtil.extractRefreshTokenFromRequest(request);
      AuthenticationResultType result = cognitoApiClientService.refreshAuthTokens(
          "e00c590c-3091-70b7-caa7-996f0133e48b", token);
      // TODO: ATENTION!!! Need to transfer "sub" parameter from cognito into method
      String accessToken = result.getAccessToken();
      String idToken = result.getIdToken();
      TokenUtil.setAuthTokensToHeaders(response, accessToken, idToken);
    } catch (NotAuthorizedException e) {
      log.error("Refresh token has expired: {}", e.getMessage(), e);
      throw new RefreshTokenExpiredException("Refresh token has expired.");
    } catch (Exception e) {
      log.error("Refresh token process was failed: {}", e.getMessage(), e);
      throw new RefreshTokenException("Refresh token process was failed.");
    }
  }
}