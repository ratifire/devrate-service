package com.ratifire.devrate.security.helper;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.ratifire.devrate.security.service.CognitoApiClientService;
import com.ratifire.devrate.security.util.TokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Helper class for managing authentication tokens in HTTP responses.
 */
@Component
@RequiredArgsConstructor
@Profile("!local")
public class AuthTokenHelper {

  private final CognitoApiClientService cognitoApiClient;
  private final RefreshTokenCookieHelper cookieHelper;

  /**
   * Sets authentication tokens (access, ID, and optionally refresh token) in the HTTP response.
   *
   * @param response                  the HttpServletResponse to update with authentication tokens
   * @param username                  the user's unique identifier
   * @param accessToken               the access token to set, or initial value if refresh is
   *                                  needed
   * @param idToken                   the ID token to set, or initial value if refresh is needed
   * @param refreshToken              the refresh token used for refreshing or setting as a cookie
   * @param isRefreshAuthTokensNeeded whether to refresh tokens using the refresh token
   * @param isSetRefreshTokenNeeded   whether to set the refresh token as a cookie in the response
   */
  public void setAuthTokensToResponse(
      HttpServletResponse response,
      String username,
      String accessToken,
      String idToken,
      String refreshToken,
      boolean isRefreshAuthTokensNeeded,
      boolean isSetRefreshTokenNeeded
  ) {
    String finalAccessToken = accessToken;
    String finalIdToken = idToken;

    if (isRefreshAuthTokensNeeded) {
      AuthenticationResultType refreshedTokens =
          cognitoApiClient.refreshAuthTokens(username, refreshToken);

      finalAccessToken = refreshedTokens.getAccessToken();
      finalIdToken = refreshedTokens.getIdToken();

    } else {
      if (StringUtils.isEmpty(finalAccessToken) || StringUtils.isEmpty(idToken)) {
        throw new IllegalArgumentException(
            "AccessToken and IdToken must not be null if refresh is not requested");
      }
    }

    TokenUtil.setAuthTokensToHeaders(response, finalAccessToken, finalIdToken);

    if (isSetRefreshTokenNeeded) {
      cookieHelper.setRefreshTokenToCookie(response, refreshToken);
    }
  }
}