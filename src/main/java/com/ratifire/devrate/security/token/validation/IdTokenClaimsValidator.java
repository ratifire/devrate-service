package com.ratifire.devrate.security.token.validation;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_AUDIENCE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL_VERIFIED;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_ACCOUNT_ACTIVE;

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
        && isEmailVerified(claimsSet)
        && isAccountActivated(claimsSet);
  }

  @Override
  public CognitoTypeToken getTokenType() {
    return CognitoTypeToken.ID_TOKEN;
  }

  private boolean isAccountActivated(JWTClaimsSet claimsSet) {
    return TokenUtil.extractStringClaim(claimsSet, ATTRIBUTE_IS_ACCOUNT_ACTIVE)
        .map(Boolean.TRUE.toString()::equals)
        .orElse(false);
  }

  private boolean isAudienceValid(JWTClaimsSet claimsSet) {
    String audience = TokenUtil.extractArrayClaim(claimsSet, ATTRIBUTE_AUDIENCE)
        .flatMap(audList -> audList.stream().findFirst())
        .orElse(null);
    return cognitoRegistrationProperties.getClientId().equals(audience);
  }

  private boolean isEmailVerified(JWTClaimsSet claimsSet) {
    return TokenUtil.extractBooleanClaim(claimsSet, ATTRIBUTE_EMAIL_VERIFIED).orElse(false);
  }
}