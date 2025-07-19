package com.ratifire.devrate.security.util;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_FAMILY_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_GIVEN_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IDENTITIES;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_LINKED_RECORD_SUBJECT;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_ROLE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_SUBJECT;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_USERNAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_USER_ID;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_AUTHORIZATION;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_ID_TOKEN;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.REFRESH_TOKEN_COOKIE_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.TOKEN_BEARER_PREFIX;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ACCESS_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ID_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.REFRESH_TOKEN;
import static com.ratifire.devrate.util.JsonConverter.deserialize;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ratifire.devrate.security.exception.InvalidTokenException;
import com.ratifire.devrate.security.exception.RefreshTokenException;
import com.ratifire.devrate.security.model.CognitoUserInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.function.ThrowingFunction;

/**
 * Utility class for extracting claims from a JWTClaimsSet.
 */
@Slf4j
public class TokenUtil {

  private TokenUtil() {
  }

  /**
   * Sets the access token and ID token as headers in the HTTP response.
   */
  public static void setAuthTokensToHeaders(HttpServletResponse response, String accessToken,
      String idToken) {
    response.setHeader(HEADER_AUTHORIZATION, TOKEN_BEARER_PREFIX + accessToken);
    response.setHeader(HEADER_ID_TOKEN, idToken);
  }

  /**
   * Extracts the access token from the Authorization header in the request.
   *
   * @param request the HTTP request containing the Authorization header
   * @return the access token or an empty string if the header is invalid or missing
   */
  public static String extractAccessTokenFromRequest(HttpServletRequest request) {
    String header = request.getHeader(HEADER_AUTHORIZATION);
    if (header != null && header.startsWith(TOKEN_BEARER_PREFIX)) {
      return header.substring(TOKEN_BEARER_PREFIX.length());
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
    return request.getHeader(HEADER_ID_TOKEN);
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
   * @return the "subject" claim as a String, representing the subject of the token
   * @throws InvalidTokenException if the "subject" claim is missing or cannot be parsed
   */
  public static String getSubjectFromAccessToken(String accessToken) {
    JWTClaimsSet claimsSet = TokenUtil.parseToken(accessToken);
    return TokenUtil.extractStringClaim(claimsSet, ATTRIBUTE_SUBJECT)
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
    String userId = TokenUtil.extractStringClaim(claimsSet, ATTRIBUTE_USER_ID)
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
    String role = TokenUtil.extractStringClaim(claimsSet, ATTRIBUTE_ROLE)
        .orElseThrow(() -> new InvalidTokenException("Role claim is missing"));
    return List.of(new SimpleGrantedAuthority(role));
  }

  /**
   * Extracts user information from the provided Cognito ID token.
   *
   * @param idToken the Cognito ID token from which user information needs to be extracted
   * @return a CognitoUserInfo containing the extracted user details
   */
  public static CognitoUserInfo getCognitoUserInfoFromIdToken(String idToken) {
    JWTClaimsSet claimsSet = parseToken(idToken);

    String firstName = extractStringClaim(claimsSet, ATTRIBUTE_GIVEN_NAME).orElse(null);
    String lastName = extractStringClaim(claimsSet, ATTRIBUTE_FAMILY_NAME).orElse(null);
    String subject = extractStringClaim(claimsSet, ATTRIBUTE_SUBJECT).orElse(null);
    String email = extractStringClaim(claimsSet, ATTRIBUTE_EMAIL).orElse(null);
    String cognitoUsername = extractStringClaim(claimsSet, ATTRIBUTE_USERNAME).orElse(null);
    String linkedRecord = extractStringClaim(claimsSet, ATTRIBUTE_LINKED_RECORD_SUBJECT)
        .orElse(null);
    String isPrimaryRecord = extractStringClaim(claimsSet, ATTRIBUTE_IS_PRIMARY_RECORD)
        .orElse(null);
    String providerName = getProviderNameFromIdentities(claimsSet.getClaim(ATTRIBUTE_IDENTITIES));

    return new CognitoUserInfo(firstName, lastName, email, subject, providerName, linkedRecord,
        cognitoUsername, isPrimaryRecord);
  }

  private static String getProviderNameFromIdentities(Object identityClaims) {
    if (ObjectUtils.isEmpty(identityClaims)) {
      return null;
    }

    try {
      List<Map<String, Object>> identities = (List<Map<String, Object>>) identityClaims;
      return identities.stream()
          .map(identity -> identity.get("providerName"))
          .filter(Objects::nonNull)
          .map(Object::toString)
          .findFirst()
          .orElse(null);
    } catch (Exception e) {
      throw new InvalidTokenException("Failed to parse 'identities' claim from token", e);
    }
  }

  /**
   * Parses the provided JWT token and retrieves the claims set.
   */
  public static JWTClaimsSet parseToken(String token) {
    try {
      SignedJWT signedJwt = SignedJWT.parse(token);
      return signedJwt.getJWTClaimsSet();
    } catch (Exception e) {
      throw new InvalidTokenException("Parse token process was failed", e);
    }
  }

  /**
   * Parses a JSON string containing token data and converts it into a map of tokens.
   *
   * @param json the JSON string containing the token data to be parsed
   * @return a map containing the token data
   */
  public static Map<String, String> parseTokensFromJson(String json) {
    Map<String, String> tokenMap = deserialize(json, new TypeReference<>() {
    });

    if (!tokenMap.containsKey(ACCESS_TOKEN.getValue())
        || !tokenMap.containsKey(ID_TOKEN.getValue())
        || !tokenMap.containsKey(REFRESH_TOKEN.getValue())) {
      throw new InvalidTokenException("Missing required tokens in response");
    }

    return tokenMap;
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