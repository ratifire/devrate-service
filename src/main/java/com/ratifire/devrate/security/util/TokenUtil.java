package com.ratifire.devrate.security.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ratifire.devrate.security.exception.InvalidTokenException;
import com.ratifire.devrate.security.exception.RefreshTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.function.ThrowingFunction;

/**
 * Utility class for extracting claims from a JWTClaimsSet.
 */
public class TokenUtil {

  private static final Logger log = LoggerFactory.getLogger(TokenUtil.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String ID_TOKEN_HEADER = "ID-Token";
  private static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh-Token";
  private static final String BEARER_PREFIX = "Bearer ";
  public static final String CLAIM_USER_ID = "custom:userId";
  public static final String CLAIM_USER_ROLE = "custom:role";
  public static final String CLAIM_SUBJECT = "sub";

  private TokenUtil() {
  }

  /**
   * Sets the access token and ID token as headers in the HTTP response.
   */
  public static void setAuthTokensToHeaders(HttpServletResponse response, String accessToken,
      String idToken) {
    response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
    response.setHeader(ID_TOKEN_HEADER, idToken);
  }

  /**
   * Extracts the access token from the Authorization header in the request.
   *
   * @param request the HTTP request containing the Authorization header
   * @return the access token or an empty string if the header is invalid or missing
   */
  public static String extractAccessTokenFromRequest(HttpServletRequest request) {
    String header = request.getHeader(AUTHORIZATION_HEADER);
    if (header != null && header.startsWith(BEARER_PREFIX)) {
      return header.substring(BEARER_PREFIX.length());
    } else {
      return null;
    }
  }

  /**
   * Extracts the ID token from the custom ID-Token header in the request.
   *
   * @param request the HTTP request containing the ID-Token header
   * @return the ID token or an empty string if the header is missing
   */
  public static String extractIdTokenFromRequest(HttpServletRequest request) {
    return request.getHeader(ID_TOKEN_HEADER);
  }

  /**
   * Extracts the refresh token from the cookies in the HTTP request.
   *
   * @param request the {@link HttpServletRequest} containing the cookies.
   * @return the value of the refresh token cookie, or {@code null} if the cookie is not found.
   */
  public static String extractRefreshTokenFromRequest(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    log.warn("No refresh token provided in the request.");
    throw new RefreshTokenException("No refresh token provided.");
  }

  /**
   * Retrieves the "subject" claim from the specified access token.
   *
   * @param accessToken the JWT access token containing user claims
   * @return the "sub" claim as a String, representing the subject of the token
   * @throws InvalidTokenException if the "sub" claim is missing or cannot be parsed
   */
  public static String getSubjectFromAccessToken(String accessToken) {
    JWTClaimsSet claimsSet = TokenUtil.parseToken(accessToken);
    return TokenUtil.extractStringClaim(claimsSet, CLAIM_SUBJECT)
        .orElseThrow(() -> new InvalidTokenException("Sub claim is missing"));
  }

  /**
   * Retrieves the user ID from the specified ID token.
   *
   * @param idToken the Cognito ID token containing user claims
   * @return the user ID as a Long
   * @throws InvalidTokenException if the user ID claim is missing or cannot be parsed
   */
  public static long getUserIdFromIdToken(String idToken) {
    JWTClaimsSet claimsSet = TokenUtil.parseToken(idToken);
    String userId = TokenUtil.extractStringClaim(claimsSet, CLAIM_USER_ID)
        .orElseThrow(() -> new InvalidTokenException("User ID claim is missing"));
    return Long.parseLong(userId);
  }

  /**
   * Retrieves the list of authorities (roles) from the specified ID token.
   *
   * @param idToken the Cognito ID token containing user claims
   * @return a list of SimpleGrantedAuthority representing the user's roles
   * @throws InvalidTokenException if the role claim is missing or cannot be parsed
   */
  public static List<SimpleGrantedAuthority> getAuthoritiesFromIdToken(String idToken) {
    JWTClaimsSet claimsSet = TokenUtil.parseToken(idToken);
    String role = TokenUtil.extractStringClaim(claimsSet, CLAIM_USER_ROLE)
        .orElseThrow(() -> new InvalidTokenException("Role claim is missing"));
    return List.of(new SimpleGrantedAuthority(role));
  }

  /**
   * Parses the provided JWT token and retrieves the claims set.
   */
  public static JWTClaimsSet parseToken(String token) {
    try {
      SignedJWT signedJwt = SignedJWT.parse(token);
      return signedJwt.getJWTClaimsSet();
    } catch (Exception e) {
      throw new InvalidTokenException("Parse token process was failed");
    }
  }

  /**
   * Extracts a String claim from the JWTClaimsSet.
   */
  public static Optional<String> extractStringClaim(JWTClaimsSet claimsSet, String claim) {
    return extractClaim(claimsSet, claim, cs -> cs.getStringClaim(claim));
  }

  /**
   * Extracts a Boolean claim from the JWTClaimsSet.
   */
  public static Optional<Boolean> extractBooleanClaim(JWTClaimsSet claimsSet, String claim) {
    return extractClaim(claimsSet, claim, cs -> cs.getBooleanClaim(claim));
  }

  /**
   * Extracts a Date claim from the JWTClaimsSet.
   */
  public static Optional<Date> extractDateClaim(JWTClaimsSet claimsSet, String claim) {
    return extractClaim(claimsSet, claim, cs -> cs.getDateClaim(claim));
  }

  /**
   * Extracts an Array claim from the JWTClaimsSet.
   */
  public static Optional<List<String>> extractArrayClaim(JWTClaimsSet claimsSet, String claim) {
    return extractClaim(claimsSet, claim, cs -> ((List<String>) cs.getClaim(claim)).stream()
        .map(Object::toString)
        .toList()
    );
  }

  private static <T> Optional<T> extractClaim(JWTClaimsSet claimsSet, String claim,
      ThrowingFunction<JWTClaimsSet, T> extractor) {
    try {
      return Optional.of(extractor.apply(claimsSet));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}