package com.ratifire.devrate.security.helper;

import static com.amazonaws.services.cognitoidp.model.AuthFlowType.REFRESH_TOKEN_AUTH;
import static com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_COGNITO_SUBJECT;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_EMAIL;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_LINKED_RECORD_SUBJECT;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_ROLE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_USER_ID;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.AUTHORIZATION_BASIC_PREFIX;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.CONTENT_TYPE_FORM_URLENCODED;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_AUTHORIZATION;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.HEADER_CONTENT_TYPE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.NONE_VALUE;
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

import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminLinkProviderForUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
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
      long userId, String role, boolean isPrimaryRecord) {
    return new SignUpRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email)
        .withPassword(password)
        .withUserAttributes(List.of(
            createAttribute(ATTRIBUTE_EMAIL, email),
            createAttribute(ATTRIBUTE_USER_ID, String.valueOf(userId)),
            createAttribute(ATTRIBUTE_ROLE, role),
            createAttribute(ATTRIBUTE_IS_PRIMARY_RECORD, String.valueOf(isPrimaryRecord)),
            createAttribute(ATTRIBUTE_LINKED_RECORD_SUBJECT, NONE_VALUE)
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
   * Builds a request to resend the registration confirmation code to a user in AWS Cognito.
   *
   * @param email the email of the user (used as the username in Cognito).
   * @return a {@link ResendConfirmationCodeRequest} object configured with the provided email.
   */
  public ResendConfirmationCodeRequest buildResendRegistrationCodeRequest(String email) {
    return new ResendConfirmationCodeRequest()
        .withClientId(cognitoConfig.getClientId())
        .withSecretHash(cognitoAuthHelper.generateSecretHash(email))
        .withUsername(email);
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
   * @param subject      the unique identifier (subject) of the user whose tokens are being
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

  /**
   * Builds a request to update user attributes in AWS Cognito for a specified user.
   *
   * @param subject             the unique identifier (subject) of the user to update.
   * @param userId              the unique identifier ID of the user.
   * @param role                the role to be assigned to the user.
   * @param isPrimaryRecord     a boolean indicating if this is the primary record of the user.
   * @param linkedRecordSubject the subject of the linked record, if any.
   * @return an AdminUpdateUserAttributesRequest configured with the provided details.
   */
  public AdminUpdateUserAttributesRequest buildAdminUpdateUserAttributesRequest(String subject,
      long userId, String role, boolean isPrimaryRecord, String linkedRecordSubject) {
    return new AdminUpdateUserAttributesRequest()
        .withUserPoolId(cognitoConfig.getUserPoolId())
        .withUsername(subject)
        .withUserAttributes(List.of(
            createAttribute(ATTRIBUTE_USER_ID, String.valueOf(userId)),
            createAttribute(ATTRIBUTE_ROLE, role),
            createAttribute(ATTRIBUTE_IS_PRIMARY_RECORD, String.valueOf(isPrimaryRecord)),
            createAttribute(ATTRIBUTE_LINKED_RECORD_SUBJECT, linkedRecordSubject)
        ));
  }

  /**
   * Builds a request for updating user attributes in AWS Cognito.
   *
   * @param attributes the list of AttributeType objects to update for the user
   * @param subject    the Cognito username
   * @return a configured AdminUpdateUserAttributesRequest with the specified attributes and user
   */
  public AdminUpdateUserAttributesRequest buildAdminUpdateUserAttributesRequest(
      List<AttributeType> attributes, String subject) {
    return new AdminUpdateUserAttributesRequest()
        .withUserPoolId(cognitoConfig.getUserPoolId())
        .withUsername(subject)
        .withUserAttributes(attributes);
  }

  /**
   * Builds a request to get user details from AWS Cognito for a specified user.
   *
   * @param username the username of the user to get.
   * @return an AdminGetUserRequest configured with the provided details.
   */
  public AdminGetUserRequest buildAdminGetUserRequest(String username) {
    return new AdminGetUserRequest()
        .withUserPoolId(cognitoConfig.getUserPoolId())
        .withUsername(username);
  }

  /**
   * Builds a request to link two users in AWS Cognito based on the specified source and destination
   * user details.
   *
   * @param destinationUser    the identifier of the user to be linked to.
   * @param sourceUserProvider the provider name of the source user.
   * @param sourceUserSubject  the unique identifier of the source user within the provider.
   * @return a configured AdminLinkProviderForUserRequest for the user link operation.
   */
  public AdminLinkProviderForUserRequest buildAdminLinkProviderForUserRequest(
      ProviderUserIdentifierType destinationUser, String sourceUserProvider,
      String sourceUserSubject) {
    AdminLinkProviderForUserRequest request = new AdminLinkProviderForUserRequest();
    request.withUserPoolId(cognitoConfig.getUserPoolId());
    request.withDestinationUser(destinationUser);

    ProviderUserIdentifierType sourceUser = new ProviderUserIdentifierType();
    sourceUser.setProviderName(sourceUserProvider);
    sourceUser.setProviderAttributeName(ATTRIBUTE_COGNITO_SUBJECT);
    sourceUser.setProviderAttributeValue(sourceUserSubject);

    request.withSourceUser(sourceUser);
    return request;
  }

  /**
   * Builds a request to list users from AWS Cognito based on a given email filter.
   *
   * @param email the email of the user to filter the list of users.
   * @return a ListUsersRequest configured with the specified email filter.
   */
  public ListUsersRequest buildListUsersRequest(String email) {
    ListUsersRequest request = new ListUsersRequest();
    request.setUserPoolId(cognitoConfig.getUserPoolId());
    request.setFilter(String.format("email=\"%s\"", email));
    return request;
  }

  /**
   * Builds a request to list users from AWS Cognito.
   *
   * @return a ListUsersRequest configured the cognito user.
   */
  public ListUsersRequest buildListUsersRequest(int limit, String paginationToken) {
    ListUsersRequest request = new ListUsersRequest();
    request.setUserPoolId(cognitoConfig.getUserPoolId());
    request.withPaginationToken(paginationToken);
    request.withLimit(limit);
    return request;
  }

  /**
   * Builds an HTTP entity for a token exchange request in AWS Cognito.
   *
   * @param authorizationCode the authorization code received during the OAuth2 authentication
   *                          flow.
   * @return a HttpEntity containing the headers and body for the token exchange request.
   */
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
   * Builds the OAuth redirect URL based on the specified OAuth2 provider and state.
   *
   * @param provider the name of the OAuth2 identity provider to use for login.
   * @param state    a unique state parameter to include in the URL for CSRF protection.
   * @return the complete OAuth redirect URL as a String.
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
   * Constructs the URL for obtaining an OAuth token using the configured Cognito domain.
   *
   * @return the complete token URL as a String
   */
  public String buildTokenUrl() {
    return UriComponentsBuilder.fromUriString(OAUTH_TOKEN_URL)
        .buildAndExpand(Map.of(PARAM_DOMAIN, cognitoConfig.getDomain()))
        .toUriString();
  }

  /**
   * Generates an authorization header containing basic authentication credentials in the format
   * "Basic {Base64EncodedCredentials}".
   *
   * @return a String representing the authorization header.
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