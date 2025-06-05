package com.ratifire.devrate.security.helper;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.REFRESH_TOKEN_COOKIE_NAME;

import com.ratifire.devrate.security.configuration.properties.CookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * A helper class for managing refresh token cookies.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenCookieHelper {

  private static final int DELETE_COOKIE_LIFE_TIME = 0;
  private final CookieProperties properties;

  /**
   * Sets the refresh token into a cookie and adds it to the HTTP response.
   *
   * @param response     the HttpServletResponse to which the cookie will be added.
   * @param refreshToken the refresh token to be set in the cookie.
   */
  public void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
    Cookie cookie = buildCookie(refreshToken, properties.getLifeTime());
    response.addCookie(cookie);
  }

  /**
   * Deletes the refresh token from the cookie by setting its value to empty.
   *
   * @param response the HttpServletResponse to which the deletion cookie will be added.
   */
  public void deleteRefreshTokenFromCookie(HttpServletResponse response) {
    Cookie cookie = buildCookie(StringUtils.EMPTY, DELETE_COOKIE_LIFE_TIME);
    response.addCookie(cookie);
  }

  private Cookie buildCookie(String value, int lifeTime) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(lifeTime);
    return cookie;
  }
}