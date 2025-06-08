package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_DEFAULT_PROVIDER_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_ACCOUNT_ACTIVE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_PROVIDER_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.NONE_VALUE;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ID_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.REFRESH_TOKEN;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.ratifire.devrate.dto.LoginResponseDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.OauthException;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.CognitoUserInfo;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.security.model.enums.AccountLanguage;
import com.ratifire.devrate.security.model.enums.LoginStatus;
import com.ratifire.devrate.security.model.enums.RegistrationSourceType;
import com.ratifire.devrate.security.util.CognitoUtil;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


/**
 * Service responsible for handling authentication and authorization via OAuth with AWS Cognito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!local")
public class AuthenticationOauthService {

  private final CognitoApiClientService cognitoApiClient;
  private final UserService userService;
  private final RegistrationService registrationService;
  private final OauthStateTokenService stateTokenService;
  private final EmailConfirmationCodeService emailConfirmationCodeService;
  private final EmailService emailService;
  private final CognitoApiClientRequestHelper apiRequestHelper;
  private final CognitoAuthenticationHelper authHelper;
  private final RefreshTokenCookieHelper cookieHelper;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Generates an OAuth redirect URL for the specified OAuth provider.
   *
   * @param oauthProvider the name of the OAuth provider
   * @return the constructed redirect URL for the specified OAuth provider
   */
  public String generateOauthRedirectUrl(String oauthProvider) {
    String signedStateToken = stateTokenService.generateSignedStateToken();
    return apiRequestHelper.buildOauthRedirectUrl(oauthProvider, signedStateToken);
  }

  /**
   * Handles the OAuth authorization flow by exchanging the authorization code for tokens,
   * processing the user information, refreshing authentication tokens, and setting the response
   * with updated tokens.
   *
   * @param response the HttpServletResponse to which the tokens will be added
   * @param request  the OauthAuthorizationDto containing the authorization code and other necessary
   *                 details for OAuth processing
   * @return a UserDto object representing the internal user mapped from the processed data
   */
  public LoginResponseDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request) {
    try {
      stateTokenService.validateStateToken(request.getState());

      Map<String, String> rawTokens =
          cognitoApiClient.fetchRawTokensFromCognito(request.getAuthorizationCode());

      String idToken = rawTokens.get(ID_TOKEN.getValue());
      String refreshToken = rawTokens.get(REFRESH_TOKEN.getValue());

      CognitoUserInfo cognitoUserInfo = TokenUtil.getCognitoUserInfoFromIdToken(idToken);

      User internalUser = processInternalUser(cognitoUserInfo);

      if (Boolean.FALSE.equals(internalUser.getAccountActivated())) {
        synchronizeAccountStatusWithCognito(cognitoUserInfo.cognitoUsername(), refreshToken,
            response);
        String activationCode = emailConfirmationCodeService.createConfirmationCode(
            internalUser.getId());
        emailService.sendAccountActivationCodeEmail(internalUser.getEmail(), activationCode);
        return buildLoginResponseDto(LoginStatus.ACTIVATION_REQUIRED, null);
      }

      AuthenticationResultType refreshedTokens =
          cognitoApiClient.refreshAuthTokens(cognitoUserInfo.cognitoUsername(), refreshToken);
      setAuthTokensToResponse(response, refreshedTokens.getAccessToken(),
          refreshedTokens.getIdToken(), refreshToken);

      return buildLoginResponseDto(LoginStatus.AUTHENTICATED, userMapper.toDto(internalUser));

    } catch (HttpClientErrorException e) {
      log.error("HTTP Error during token exchange: {}", e.getMessage(), e);
      throw new OauthException(
          "OAuth authentication process failed during the exchange of code for tokens.", e);
    } catch (AWSCognitoIdentityProviderException e) {
      log.error("Cognito Error during OAuth authorization process: {}", e.getMessage(), e);
      throw new OauthException("OAuth authentication process failed. Cognito error: " + e);
    } catch (RuntimeException e) {
      log.error("Error during OAuth authorization process: {}", e.getMessage(), e);
      throw new OauthException("OAuth authentication process failed.", e);
    }
  }

  private User processInternalUser(CognitoUserInfo userInfo) {
    User internalUser = userService.findByEmail(userInfo.email());

    if (ObjectUtils.isNotEmpty(internalUser)) {
      linkAndSynchronizeInternalUserWithCognito(internalUser, userInfo);
      internalUser.setRegistrationSource(RegistrationSourceType.FEDERATED_IDENTITY);
      userService.updateByEntity(internalUser);
    } else {
      internalUser = createInternalUser(userInfo);
    }

    return internalUser;
  }

  private void linkAndSynchronizeInternalUserWithCognito(
      User internalUser,
      CognitoUserInfo userInfo) {
    Optional<ProviderUserIdentifierType> cognitoPrimaryUserOptional =
        findCognitoPrimaryUserByEmail(userInfo.email());

    if (cognitoPrimaryUserOptional.isEmpty()) {
      throw new OauthException("Primary user not found");
    }

    ProviderUserIdentifierType cognitoPrimaryUser = cognitoPrimaryUserOptional.get();
    String cognitoPrimaryUserSubject = cognitoPrimaryUser.getProviderAttributeValue();

    if (areCognitoUsersLinked(userInfo.linkedRecord(), userInfo.isPrimaryRecord(),
        cognitoPrimaryUserSubject)) {
      return;
    }
    linkCognitoUsersInPool(cognitoPrimaryUser, userInfo);
    cognitoApiClient.updateCognitoUserAttributes(userInfo.subject(), internalUser.getId(),
        AccessLevel.getDefaultRole(), false, cognitoPrimaryUserSubject,
        userInfo.isAccountActivated());
  }

  private void synchronizeAccountStatusWithCognito(String username, String refreshToken,
      HttpServletResponse response) {
    cognitoApiClient.updateCognitoUserAttributes(
        List.of(CognitoUtil.createAttribute(ATTRIBUTE_IS_ACCOUNT_ACTIVE, Boolean.FALSE.toString())),
        username);
    AuthenticationResultType refreshedTokens =
        cognitoApiClient.refreshAuthTokens(username, refreshToken);
    setAuthTokensToResponse(response, refreshedTokens.getAccessToken(),
        refreshedTokens.getIdToken(), refreshToken);
  }

  private User createInternalUser(CognitoUserInfo userInfo) {
    String email = userInfo.email();
    UserDto userDto = UserDto.builder()
        .firstName(userInfo.firstName())
        .lastName(userInfo.lastName())
        .registrationSource(RegistrationSourceType.FEDERATED_IDENTITY)
        .accountLanguage(AccountLanguage.UKRAINE)
        .accountActivated(true)
        .build();
    User newUser = userService.create(userDto, email, authHelper.generateRandomPassword());
    registrationService.finalizeUserRegistration(newUser, email);
    cognitoApiClient.updateCognitoUserAttributes(userInfo.subject(), newUser.getId(),
        AccessLevel.getDefaultRole(), true, NONE_VALUE, Boolean.TRUE.toString());
    return newUser;
  }

  private Optional<ProviderUserIdentifierType> findCognitoPrimaryUserByEmail(String email) {
    ListUsersResult response = cognitoApiClient.getListCognitoUsersByEmail(email);
    return response.getUsers().stream()
        .filter(user -> user.getAttributes().stream()
            .anyMatch(attribute ->
                StringUtils.equals(attribute.getName(), ATTRIBUTE_IS_PRIMARY_RECORD)
                    && StringUtils.equals(attribute.getValue(), Boolean.TRUE.toString())
            ))
        .findFirst()
        .map(this::mapToProviderUserIdentifier);
  }

  private boolean areCognitoUsersLinked(String cognitoUserLinkedRecord, String isPrimaryRecord,
      String cognitoPrimaryUserSubject) {
    if (StringUtils.isEmpty(cognitoUserLinkedRecord)) {
      return false;
    }

    if (StringUtils.equals(cognitoUserLinkedRecord, cognitoPrimaryUserSubject)
        || Boolean.TRUE.toString().equals(isPrimaryRecord)) {
      return true;
    }

    throw new OauthException(String.format(
        "Cognito pool user linked record (%s) does not match the primary pool user subject (%s)",
        cognitoUserLinkedRecord, cognitoPrimaryUserSubject));
  }

  private void linkCognitoUsersInPool(ProviderUserIdentifierType destinationUser,
      CognitoUserInfo userInfo) {
    cognitoApiClient.linkCognitoUsersInPool(destinationUser, userInfo.provider(),
        userInfo.subject());
  }

  private ProviderUserIdentifierType mapToProviderUserIdentifier(UserType user) {
    ProviderUserIdentifierType providerUser = new ProviderUserIdentifierType();
    providerUser.setProviderName(getProviderNameAttribute(user.getAttributes()));
    providerUser.setProviderAttributeValue(user.getUsername());
    return providerUser;
  }

  private String getProviderNameAttribute(List<AttributeType> attributes) {
    return attributes.stream()
        .filter(attribute -> StringUtils.equals(attribute.getName(),
            ATTRIBUTE_PROVIDER_NAME))
        .map(AttributeType::getValue)
        .findFirst()
        .orElse(ATTRIBUTE_DEFAULT_PROVIDER_NAME);
  }

  private void setAuthTokensToResponse(
      HttpServletResponse response,
      String accessToken,
      String idToken,
      String refreshToken) {
    TokenUtil.setAuthTokensToHeaders(response, accessToken, idToken);
    cookieHelper.setRefreshTokenToCookie(response, refreshToken);
  }

  private LoginResponseDto buildLoginResponseDto(LoginStatus status, UserDto user) {
    return LoginResponseDto.builder()
        .status(status)
        .userInfo(user)
        .build();
  }
}