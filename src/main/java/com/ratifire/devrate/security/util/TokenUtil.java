package com.ratifire.devrate.security.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ratifire.devrate.security.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.function.ThrowingFunction;

/**
 * Utility class for extracting claims from a JWTClaimsSet.
 */
public class TokenUtil {


  private TokenUtil() {
  }

  /**
   * Extracts the access token from the Authorization header in the request.
   *
   * @param request the HTTP request containing the Authorization header
   * @return the access token or an empty string if the header is invalid or missing
   */
  public static String extractAccessTokenFromRequest(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring("Bearer ".length());
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
    return request.getHeader("ID-Token");
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
    return TokenUtil.extractLongClaim(claimsSet, "custom:userId")
        .orElseThrow(() -> new InvalidTokenException("User ID claim is missing"));
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
    String role = TokenUtil.extractStringClaim(claimsSet, "custom:role")
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
      throw new InvalidTokenException("Failed to parse token");
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
   * Extracts a Long claim from the JWTClaimsSet.
   */
  public static Optional<Long> extractLongClaim(JWTClaimsSet claimsSet, String claim) {
    return extractClaim(claimsSet, claim, cs -> cs.getLongClaim(claim));
  }

  /**
   * Extracts a Date claim from the JWTClaimsSet.
   */
  public static Optional<Date> extractDateClaim(JWTClaimsSet claimsSet, String claim) {
    return extractClaim(claimsSet, claim, cs -> cs.getDateClaim(claim));
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