package com.ratifire.devrate.security.helper;

import static com.amazonaws.services.cognitoidp.model.AuthFlowType.REFRESH_TOKEN_AUTH;
import static com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_ROLE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_USER_ID;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.AUTHORIZATION_BASIC_PREFIX;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.CONTENT_TYPE_FORM_URLENCODED;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_AUTHORIZATION;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_CONTENT_TYPE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.OAUTH_AUTHORIZE_URL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.OAUTH_TOKEN_URL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_CLIENT_ID;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_CODE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_DOMAIN;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_GRANT_TYPE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_IDENTITY_PROVIDER;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_PASSWORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_REDIRECT_URI;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_REFRESH_TOKEN;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_RESPONSE_TYPE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_SCOPE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_SECRET_HASH;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_STATE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.PARAM_USERNAME;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.ratifire.devrate.security.configuration.properties.CognitoRegistrationProperties;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Helper class for building AWS Cognito API requests.
 */
@Component
@RequiredArgsConstructor
public class CognitoApiClientRequestHelper {

  private final CognitoAuthenticationHelper cognitoAuthHelper;
  private final CognitoRegistrationProperties cognitoConfig;

  /**
   * Builds a request to register a new user in AWS Cognito.
   *
   * @param email    the email of the user (used as the username in Cognito).
   * @param password the password of the user.
   * @param userId   the unique identifier of the user.
   * @param role     the role assigned to the user.
   * @return a {@link SignUpRequest} object configured with the provided details.
   */
  public SignUpRequest buildRegisterRequest(String email, String password,
      long userId, String role) {
    return new SignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withPassword(password)
        .withUserAttributes(List.of(
            createAttribute(ATTRIBUTE_EMAIL, email),
            createAttribute(ATTRIBUTE_USER_ID, String.valueOf(userId)),
            createAttribute(ATTRIBUTE_ROLE, role)
        ));
  }

  /**
   * Builds a request to confirm a user's registration in AWS Cognito.
   *
   * @param email            the email of the user (username).
   * @param confirmationCode the confirmation code sent to the user.
   * @return a {@link ConfirmSignUpRequest} object configured with the provided details.
   */
  public ConfirmSignUpRequest buildConfirmRegistrationRequest(String email,
      String confirmationCode) {
    return new ConfirmSignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withConfirmationCode(confirmationCode);
  }

  /**
   * Builds a request to initiate a password reset for a user in AWS Cognito.
   *
   * @param email the email of the user (username).
   * @return a {@link ForgotPasswordRequest} object configured with the provided details.
   */
  public ForgotPasswordRequest buildResetPasswordRequest(String email) {
    return new ForgotPasswordRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email);
  }

  /**
   * Builds a request to confirm a password reset for a user in AWS Cognito.
   *
   * @param email            the email of the user (username).
   * @param confirmationCode the confirmation code sent to the user.
   * @param newPassword      the new password for the user.
   * @return a {@link ConfirmForgotPasswordRequest} object configured with the provided details.
   */
  public ConfirmForgotPasswordRequest buildConfirmResetPasswordRequest(String email,
      String confirmationCode, String newPassword) {
    return new ConfirmForgotPasswordRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withConfirmationCode(confirmationCode)
        .withPassword(newPassword);
  }

  /**
   * Builds a request to log in a user using their email and password.
   *
   * @param email    the email of the user (username).
   * @param password the password of the user.
   * @return an {@link InitiateAuthRequest} object configured with the provided details.
   */
  public InitiateAuthRequest buildLoginRequest(String email, String password) {
    return new InitiateAuthRequest()
        .withClientId(cognitoConfig.getClientId())
        .withAuthFlow(USER_PASSWORD_AUTH)
        .withAuthParameters(Map.of(
            PARAM_USERNAME, email,
            PARAM_PASSWORD, password,
            PARAM_SECRET_HASH, cognitoAuthHelper.generateSecretHash(email)
        ));
  }

  /**
   * Builds a request to refresh authentication tokens using a refresh token.
   *
   * @param subject          the unique identifier (subject) of the user whose tokens are being
   *                     refreshed.
   * @param refreshToken the refresh token issued during initial authentication.
   * @return an {@link InitiateAuthRequest} object configured with the provided details.
   */
  public InitiateAuthRequest buildRefreshAuthRequest(String subject, String refreshToken) {
    return new InitiateAuthRequest()
        .withClientId(cognitoConfig.getClientId())
        .withAuthFlow(REFRESH_TOKEN_AUTH)
        .withAuthParameters(Map.of(
            PARAM_REFRESH_TOKEN, refreshToken,
            PARAM_USERNAME, subject,
            PARAM_SECRET_HASH, cognitoAuthHelper.generateSecretHash(subject)
        ));
  }

  /**
   * Builds a request to log out a user by invalidating their current session.
   *
   * @param accessToken the access token of the user's current session.
   * @return a {@link GlobalSignOutRequest} object configured with the provided details.
   */
  public GlobalSignOutRequest buildLogoutRequest(String accessToken) {
    return new GlobalSignOutRequest()
        .withAccessToken(accessToken);
  }

  public AdminUpdateUserAttributesRequest buildAdminUpdateUserAttributesRequest(String subject,
      long userId, String role) {
    return new AdminUpdateUserAttributesRequest()
        .withUserPoolId(cognitoConfig.getUserPoolId())
        .withUsername(subject)
        .withUserAttributes(List.of(
            createAttribute(ATTRIBUTE_USER_ID, String.valueOf(userId)),
            createAttribute(ATTRIBUTE_ROLE, role)
        ));
  }

  public HttpEntity<MultiValueMap<String, String>> buildTokenExchangeRequest(
      String authorizationCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED);
    headers.set(HEADER_AUTHORIZATION, generateAuthorizationHeader());
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add(PARAM_GRANT_TYPE, cognitoConfig.getAuthorizationGrantType());
    body.add(PARAM_CODE, authorizationCode);
    body.add(PARAM_REDIRECT_URI, cognitoConfig.getRedirectUri());
    return new HttpEntity<>(body, headers);
  }

  /**
   * test sso.
   */
  public String buildOauthRedirectUrl(String provider, String state) {
    return UriComponentsBuilder.fromUriString(OAUTH_AUTHORIZE_URL)
        .queryParam(PARAM_RESPONSE_TYPE, PARAM_CODE)
        .queryParam(PARAM_CLIENT_ID, cognitoConfig.getClientId())
        .queryParam(PARAM_REDIRECT_URI, cognitoConfig.getRedirectUri())
        .queryParam(PARAM_SCOPE, cognitoConfig.getScope())
        .queryParam(PARAM_IDENTITY_PROVIDER, provider)
        .queryParam(PARAM_STATE, state)
        .buildAndExpand(Map.of(PARAM_DOMAIN, cognitoConfig.getDomain()))
        .toUriString();
  }

  /**
   * test sso.
   */
  public String buildTokenUrl() {
    return UriComponentsBuilder.fromUriString(OAUTH_TOKEN_URL)
        .buildAndExpand(Map.of(PARAM_DOMAIN, cognitoConfig.getDomain()))
        .toUriString();
  }

  /**
   * test sso.
   */
  private String generateAuthorizationHeader() {
    String credentials = String.format("%s:%s", cognitoConfig.getClientId(),
        cognitoConfig.getClientSecret());
    return AUTHORIZATION_BASIC_PREFIX + Base64.getEncoder()
        .encodeToString(credentials.getBytes(UTF_8));
  }

  private AttributeType createAttribute(String name, String value) {
    return new AttributeType()
        .withName(name)
        .withValue(value);
  }
}