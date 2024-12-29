package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_PROVIDER_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.NONE_VALUE;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ACCESS_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.ID_TOKEN;
import static com.ratifire.devrate.security.model.enums.CognitoTypeToken.REFRESH_TOKEN;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.helper.CognitoApiClientRequestHelper;
import com.ratifire.devrate.security.helper.CognitoAuthenticationHelper;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.model.CognitoUserInfo;
import com.ratifire.devrate.security.model.dto.OauthAuthorizationDto;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * test sso.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationOauthService {

  private final CognitoApiClientService cognitoApiClient;
  private final UserService userService;
  private final CognitoApiClientRequestHelper apiRequestHelper;
  private final CognitoAuthenticationHelper authHelper;
  private final RefreshTokenCookieHelper cookieHelper;
  private final DataMapper<UserDto, User> userMapper;

  public String generateOauthRedirectUrl(HttpSession session, String oauthProvider) {
    String oauthState = UUID.randomUUID().toString();
    session.setAttribute("oauth2State", oauthState);
    return apiRequestHelper.buildOauthRedirectUrl(oauthProvider, oauthState);
  }

  public UserDto handleOauthAuthorization(HttpServletResponse response,
      OauthAuthorizationDto request) {
    try {
      Map<String, String> rawTokens =
          cognitoApiClient.fetchRawTokensFromCognito(request.getAuthorizationCode());

      String accessToken = rawTokens.get(ACCESS_TOKEN.getValue());
      String idToken = rawTokens.get(ID_TOKEN.getValue());
      String refreshToken = rawTokens.get(REFRESH_TOKEN.getValue());

      CognitoUserInfo cognitoUserInfo = TokenUtil.getUserInfoFromIdToken(idToken);

      User internalUser = processInternalUser(response, accessToken, idToken, refreshToken,
          cognitoUserInfo);

      AuthenticationResultType refreshedTokens =
          cognitoApiClient.refreshAuthTokens(cognitoUserInfo.cognitoUsername(), refreshToken);
      setAuthTokensToResponse(response, refreshedTokens.getAccessToken(),
          refreshedTokens.getIdToken(), refreshToken);

      return userMapper.toDto(internalUser);

    } catch (HttpClientErrorException e) {
      log.error("HTTP Error during token exchange: {}", e.getResponseBodyAsString(), e);
      throw new AuthenticationException("Failed to exchange authorization code for tokens.");
    } catch (Exception e) {
      log.error("Error during token exchange or user processing: {}", e.getMessage(), e);
      throw new AuthenticationException("Authentication process failed.");
    }
  }

  private User processInternalUser(HttpServletResponse response, String accessToken, String idToken,
      String refreshToken, CognitoUserInfo userInfo) {
    User internalUser = userService.findByEmail(userInfo.email());

    if (ObjectUtils.isNotEmpty(internalUser)) {
      linkAndSynchronizeInternalUserWithCognito(response, internalUser, accessToken, idToken,
          refreshToken, userInfo);
    } else {
      internalUser = createInternalUser(userInfo);
    }

    return internalUser;
  }

  private void linkAndSynchronizeInternalUserWithCognito(HttpServletResponse response,
      User internalUser, String accessToken, String idToken, String refreshToken,
      CognitoUserInfo userInfo) {
    Optional<ProviderUserIdentifierType> cognitoPrimaryUserOptional =
        findCognitoPrimaryUserByEmail(userInfo.email());

    if (cognitoPrimaryUserOptional.isEmpty()) {
      throw new AuthenticationException("User not found");
    }

    ProviderUserIdentifierType cognitoPrimaryUser = cognitoPrimaryUserOptional.get();
    String cognitoPrimaryUserSubject = cognitoPrimaryUser.getProviderAttributeValue();
    if (areCognitoUsersLinked(userInfo.linkedRecord(), cognitoPrimaryUserSubject)) {
      setAuthTokensToResponse(response, accessToken, idToken, refreshToken);
      return;
    }
    linkCognitoUsersInPool(cognitoPrimaryUser, userInfo);
    cognitoApiClient.updateCognitoUserAttributes(userInfo.subject(), internalUser.getId(),
        AccessLevel.getDefaultRole(), false, cognitoPrimaryUserSubject);
  }

  private User createInternalUser(CognitoUserInfo userInfo) {
    UserDto userDto = UserDto.builder()
        .firstName(userInfo.firstName())
        .lastName(userInfo.lastName())
        .country("Ukraine")
        .subscribed(false)
        .build();
    User newUser = userService.create(userDto, userInfo.email(),
        authHelper.generateRandomPassword());
    cognitoApiClient.updateCognitoUserAttributes(userInfo.subject(), newUser.getId(),
        AccessLevel.getDefaultRole(), true, NONE_VALUE);
    return newUser;
  }

  private Optional<ProviderUserIdentifierType> findCognitoPrimaryUserByEmail(String email) {
    ListUsersResult response = cognitoApiClient.getListCognitoUsersByEmail(email);
    return response.getUsers().stream()
        .filter(user -> user.getAttributes().stream()
            .anyMatch(attribute ->
                StringUtils.equals(attribute.getName(), ATTRIBUTE_IS_PRIMARY_RECORD) &&
                    StringUtils.equals(attribute.getValue(), "true")
            ))
        .findFirst()
        .map(this::mapToProviderUserIdentifier);
  }

  private boolean areCognitoUsersLinked(String cognitoUserLinkedRecord,
      String cognitoPrimaryUserSubject) {
    if (StringUtils.isEmpty(cognitoUserLinkedRecord)) {
      return false;
    }

    if (StringUtils.equals(cognitoUserLinkedRecord, cognitoPrimaryUserSubject)) {
      return true;
    }
    throw new IllegalStateException(String.format(
        "Pool user linked record (%s) does not match the primary pool user subject (%s)",
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
        .orElse("Cognito");
  }

  private void setAuthTokensToResponse(HttpServletResponse response, String accessToken,
      String idToken, String refreshToken) {
    TokenUtil.setAuthTokensToHeaders(response, accessToken, idToken);
    cookieHelper.setRefreshTokenToCookie(response, refreshToken);
  }
}