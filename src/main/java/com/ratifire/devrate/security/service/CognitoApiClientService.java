package com.ratifire.devrate.security.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminLinkProviderForUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import com.ratifire.devrate.security.util.TokenUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 * Service class for interacting with AWS Cognito API.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class CognitoApiClientService {

  private final RestTemplateBuilder restTemplate;
  private final AWSCognitoIdentityProvider cognitoClient;
  private final CognitoApiClientRequestHelper requestHelper;

  /**
   * Registers a new user in Cognito with the provided email, password, user ID, and role.
   *
   * @param email    The user's email, which will serve as the username.
   * @param password The user's password.
   * @param userId   The unique identifier for the user.
   * @param role     The user's role.
   */
  public void register(String email, String password, long userId, String role,
      boolean isPrimaryRecord) {
    SignUpRequest request = requestHelper.buildRegisterRequest(email, password, userId, role,
        isPrimaryRecord);
    cognitoClient.signUp(request);
  }

  /**
   * Confirms user registration in Cognito using the provided confirmation code.
   *
   * @param email The user's email (username).
   * @param code  The confirmation code sent to the user's email.
   */
  public void confirmRegistration(String email, String code) {
    ConfirmSignUpRequest request = requestHelper.buildConfirmRegistrationRequest(email, code);
    cognitoClient.confirmSignUp(request);
  }

  /**
   * Resends the registration confirmation code to the specified user's email.
   *
   * @param email The user's email to which the confirmation code will be sent.
   */
  public void resendRegistrationCode(String email) {
    ResendConfirmationCodeRequest request = requestHelper.buildResendRegistrationCodeRequest(email);
    cognitoClient.resendConfirmationCode(request);
  }

  /**
   * Authenticates an existing user in Cognito with the provided email and password.
   *
   * @param email    The user's email (username).
   * @param password The user's password.
   * @return An {@link AuthenticationResultType} object containing authentication details such as
   *     access token, refresh token, and ID token.
   */
  public AuthenticationResultType login(String email, String password) {
    InitiateAuthRequest request = requestHelper.buildLoginRequest(email, password);
    return cognitoClient.initiateAuth(request).getAuthenticationResult();
  }

  /**
   * Logs out a user by invalidating their current session using the provided access token.
   *
   * @param accessToken The access token of the user to be logged out.
   */
  public void logout(String accessToken) {
    GlobalSignOutRequest request = requestHelper.buildLogoutRequest(accessToken);
    cognitoClient.globalSignOut(request);
  }

  /**
   * Initiates the password reset process by sending a confirmation code to the user's email.
   *
   * @param email The user's email (username).
   */
  public void resetPassword(String email) {
    ForgotPasswordRequest request = requestHelper.buildResetPasswordRequest(email);
    cognitoClient.forgotPassword(request);
  }

  /**
   * Completes the password reset process using the provided confirmation code and new password.
   *
   * @param email            The user's email (username).
   * @param confirmationCode The confirmation code sent to the user's email.
   * @param newPassword      The new password for the user.
   */
  public void confirmResetPassword(String email, String confirmationCode, String newPassword) {
    ConfirmForgotPasswordRequest request =
        requestHelper.buildConfirmResetPasswordRequest(email, confirmationCode, newPassword);
    cognitoClient.confirmForgotPassword(request);
  }

  /**
   * Refreshes the user's authentication tokens using a refresh token.
   *
   * @param subject      the unique identifier (subject) of the user whose tokens are being
   *                     refreshed.
   * @param refreshToken the refresh token issued during the initial authentication process.
   * @return an {@link AuthenticationResultType} object containing the new access token, ID token,
   *     and other authentication details.
   */
  public AuthenticationResultType refreshAuthTokens(String subject, String refreshToken) {
    InitiateAuthRequest request = requestHelper.buildRefreshAuthRequest(subject, refreshToken);
    return cognitoClient.initiateAuth(request).getAuthenticationResult();
  }

  /**
   * Fetches raw tokens from Cognito using the provided authorization code.
   *
   * @param code The authorization code used to exchange for access and refresh tokens.
   * @return A map containing the raw tokens retrieved from Cognito.
   */
  public Map<String, String> fetchRawTokensFromCognito(String code) {
    String url = requestHelper.buildTokenUrl();
    HttpEntity<MultiValueMap<String, String>> request =
        requestHelper.buildTokenExchangeRequest(code);

    ResponseEntity<String> response = restTemplate.build()
        .postForEntity(url, request, String.class);
    return TokenUtil.parseTokensFromJson(response.getBody());
  }

  /**
   * Gets the details of a Cognito user by their username.
   *
   * @param username The username of the Cognito user.
   */
  public AdminGetUserResult getCognitoUserDetails(String username) {
    return cognitoClient.adminGetUser(requestHelper.buildAdminGetUserRequest(username));
  }

  /**
   * Updates the attributes of a Cognito user with the specified details.
   *
   * @param subject             The subject identifier for the Cognito user.
   * @param userId              The unique ID of the user whose attributes are being updated.
   * @param role                The role of the user in the application.
   * @param isPrimaryRecord     A flag indicating if this is the primary record for the user.
   * @param linkedRecordSubject The subject identifier of the linked record, if any.
   */
  public void updateCognitoUserAttributes(String subject, long userId, String role,
      boolean isPrimaryRecord, String linkedRecordSubject) {
    cognitoClient.adminUpdateUserAttributes(
        requestHelper.buildAdminUpdateUserAttributesRequest(subject, userId, role, isPrimaryRecord,
            linkedRecordSubject));
  }

  /**
   * Updates user attributes in AWS Cognito for the specified user.
   *
   * @param attributesToUpdate the list of AttributeType values to be updated
   * @param subject            the Cognito username
   */
  public void updateCognitoUserAttributes(List<AttributeType> attributesToUpdate, String subject) {
    cognitoClient.adminUpdateUserAttributes(
        requestHelper.buildAdminUpdateUserAttributesRequest(attributesToUpdate, subject));
  }

  /**
   * Changes the password for an authenticated user in Cognito.
   *
   * @param accessToken the access token of the user.
   * @param oldPassword the user's current password.
   * @param newPassword the new password to set for the user.
   */
  public void changePassword(String accessToken, String oldPassword, String newPassword) {
    ChangePasswordRequest request = requestHelper.buildChangePasswordRequest(
        accessToken, oldPassword, newPassword);
    cognitoClient.changePassword(request);
  }

  /**
   * Links a user from one identity provider to another within the specified Cognito user pool.
   *
   * @param destinationUser The identifier of the destination user in the Cognito user pool.
   * @param provider        The name of the identity provider of the source user to be linked.
   * @param subject         The unique identifier of the source user in the specified identity
   *                        provider.
   */
  public void linkCognitoUsersInPool(ProviderUserIdentifierType destinationUser, String provider,
      String subject) {
    AdminLinkProviderForUserRequest request = requestHelper.buildAdminLinkProviderForUserRequest(
        destinationUser, provider, subject);
    cognitoClient.adminLinkProviderForUser(request);
  }

  /**
   * Retrieves a list of Cognito users filtered by the specified email address.
   *
   * @param email The email address used as a filter to list Cognito users.
   * @return A ListUsersResult object containing the users that match the specified email filter.
   */
  public ListUsersResult getListCognitoUsersByEmail(String email) {
    ListUsersRequest request = requestHelper.buildListUsersRequest(email);
    return cognitoClient.listUsers(request);
  }

  /**
   * Retrieves a list of Cognito users.
   *
   * @return A ListUsersResult object containing the users.
   */
  public ListUsersResult getAllListCognitoUsersWithPagination(int limit, String paginationToken) {
    ListUsersRequest request = requestHelper.buildListUsersRequest(limit, paginationToken);
    return cognitoClient.listUsers(request);
  }
}
