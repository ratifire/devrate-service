package com.ratifire.devrate.security.validation.token.validator;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.security.configuration.properties.CognitoProviderProperties;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import com.ratifire.devrate.security.model.enums.CognitoTypeToken;
import com.ratifire.devrate.security.util.TokenUtil;
import org.springframework.stereotype.Component;

/**
 * Validator for validating claims of id tokens from AWS Cognito.
 */
@Component
public class IdTokenClaimsValidator extends BaseTokenClaimsValidator {

  private static final String EXPECTED_TOKEN_USE = "id";

  /**
   * Constructs an IdTokenClaimsValidator with the specified properties for validating access
   * tokens.
   */
  public IdTokenClaimsValidator(CognitoProviderProperties cognitoProviderProperties,
      CognitoRegistrationProperties cognitoRegistrationProperties) {
    super(cognitoProviderProperties, cognitoRegistrationProperties);
  }

  @Override
  public boolean validate(JWTClaimsSet claimsSet) {
    return validateCommonClaims(claimsSet, EXPECTED_TOKEN_USE)
        && isAudienceValid(claimsSet)
        && isEmailVerified(claimsSet);
  }

  @Override
  public CognitoTypeToken getTokenType() {
    return CognitoTypeToken.ID_TOKEN;
  }

  private boolean isAudienceValid(JWTClaimsSet claimsSet) {
    String audience = TokenUtil.extractArrayClaim(claimsSet, "aud")
        .flatMap(audList -> audList.stream().findFirst())
        .orElse(null);
    return cognitoRegistrationProperties.getClientId().equals(audience);
  }

  private boolean isEmailVerified(JWTClaimsSet claimsSet) {
    return TokenUtil.extractBooleanClaim(claimsSet, "email_verified").orElse(false);
  }
}