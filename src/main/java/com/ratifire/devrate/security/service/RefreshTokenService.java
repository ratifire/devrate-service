package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.ratifire.devrate.security.exception.RefreshTokenException;
import com.ratifire.devrate.security.exception.RefreshTokenExpiredException;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling the refresh token process.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!local")
public class RefreshTokenService {

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
      String refreshToken = TokenUtil.extractRefreshTokenFromRequest(request);
      String accessToken = TokenUtil.extractAccessTokenFromRequest(request);
      String subject = TokenUtil.getSubjectFromAccessToken(accessToken);

      AuthenticationResultType result = cognitoApiClientService
          .refreshAuthTokens(subject, refreshToken);

      String newAccessToken = result.getAccessToken();
      String newIdToken = result.getIdToken();
      TokenUtil.setAuthTokensToHeaders(response, newAccessToken, newIdToken);

    } catch (NotAuthorizedException e) {
      log.error("Refresh token has expired: {}", e.getMessage(), e);
      throw new RefreshTokenExpiredException("Refresh token has expired.");
    } catch (Exception e) {
      log.error("Refresh token process was failed: {}", e.getMessage(), e);
      throw new RefreshTokenException("Refresh token process was failed.");
    }
  }
}
