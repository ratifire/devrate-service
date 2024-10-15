package com.ratifire.devrate.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * test.
 */
@Component
public class CookieHelper {

  @Value("${jwt.refresh.token.cookie.maxAge}")
  private int cookieMaxAge;

  public static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh-Token";

  /**
   * Creates an HTTP-only, secure cookie containing the refresh token.
   *
   * @param refreshToken The refresh token to be stored in the cookie.
   * @return A configured {@link Cookie} object with the refresh token.
   */
  public Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(cookieMaxAge);
    return cookie;
  }

  /**
   * Deletes the refresh token cookie by setting its max age to 0, which signals the
   * browser to remove the cookie.
   *
   * @param response The {@link HttpServletResponse} to which the cookie will be added.
   */
  public void deleteRefreshTokenCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}