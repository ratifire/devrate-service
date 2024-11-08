package com.ratifire.devrate.security.util;

import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

/**
 * Utility class for extracting claims from a JWTClaimsSet.
 */
public class TokenUtil {


  private TokenUtil() {
  }

  /**
   * Extracts a String claim from the JWTClaimsSet.
   */
  public static Optional<String> extractStringClaim(JWTClaimsSet claimsSet, String claim) {
    try {
      return Optional.ofNullable(claimsSet.getStringClaim(claim));
    } catch (ParseException e) {
      return Optional.empty();
    }
  }

  /**
   * Extracts a Boolean claim from the JWTClaimsSet.
   */
  public static Optional<Boolean> extractBooleanClaim(JWTClaimsSet claimsSet, String claim) {
    try {
      return Optional.ofNullable(claimsSet.getBooleanClaim(claim));
    } catch (ParseException e) {
      return Optional.empty();
    }
  }

  /**
   * Extracts a Number claim from the JWTClaimsSet.
   */
  public static Optional<Integer> extractIntegerClaim(JWTClaimsSet claimsSet, String claim) {
    try {
      return Optional.ofNullable(claimsSet.getIntegerClaim(claim));
    } catch (ParseException e) {
      return Optional.empty();
    }
  }

  /**
   * Extracts a Date claim from the JWTClaimsSet.
   */
  public static Optional<Date> extractDateClaim(JWTClaimsSet claimsSet, String claim) {
    try {
      return Optional.ofNullable(claimsSet.getDateClaim(claim));
    } catch (ParseException e) {
      return Optional.empty();
    }
  }
}