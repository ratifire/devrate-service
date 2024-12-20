package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.util.TokenUtil.parseToken;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminLinkProviderForUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.security.exception.AuthenticationException;
import com.ratifire.devrate.security.exception.InvalidTokenException;
import com.ratifire.devrate.security.helper.CognitoOauthHelper;
import com.ratifire.devrate.security.helper.RefreshTokenCookieHelper;
import com.ratifire.devrate.security.util.TokenUtil;
import com.ratifire.devrate.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * test sso.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoOauthService {

  private final CognitoOauthHelper cognitoOauthHelper;
  private final UserService userService;
  private final RestTemplateBuilder restTemplate;
  private final AWSCognitoIdentityProvider cognitoIdentityProvider;
  private final RefreshTokenCookieHelper refreshTokenCookieHelper;
  private final DataMapper<UserDto, User> userMapper;

  /**
   * Generates the authorization URL for the specified provider (Google, LinkedIn, etc.).
   *
   * @param session      the current session to store the unique state
   * @param providerName the name of the identity provider
   * @return the authorization URL
   */
  public String generateAuthorizationUrl(HttpSession session, String providerName) {
    String state = UUID.randomUUID().toString();
    session.setAttribute("oauth2State", state);

    String authorizationUrl = cognitoOauthHelper.buildAuthorizationUrl(providerName, state);
    log.info("Generated Authorization URL for {}: {}", providerName, authorizationUrl);
    return authorizationUrl;
  }

  /**
   * test sso.
   */
  public UserDto exchangeAuthorizationCodeForTokens(String authorizationCode,
      HttpServletResponse response) {
    try {
      String tokenUrl = cognitoOauthHelper.buildTokenUrl();
      var tokenRequest = cognitoOauthHelper.buildTokenExchangeRequest(authorizationCode);

      ResponseEntity<String> cognitoResponse = restTemplate.build()
          .postForEntity(tokenUrl, tokenRequest, String.class);
      log.info("Token exchange successful. Response: {}", cognitoResponse.getBody());

      Map<String, String> tokens = parseTokens(cognitoResponse.getBody());
      String accessToken = tokens.get("access_token");
      String idToken = tokens.get("id_token");
      String refreshToken = tokens.get("refresh_token");

      TokenUtil.setAuthTokensToHeaders(response, accessToken, idToken);
      refreshTokenCookieHelper.setRefreshTokenToCookie(response, refreshToken);

      Map<String, Object> userInfo = decodeIdToken(idToken);
      String email = (String) userInfo.get("email");
      String sub = (String) userInfo.get("sub");
      String providerName = "LinkedIn";

      User existingUser = userService.findByEmail(email);
      if (existingUser == null) {
        log.info("User not found in database. Registering new user with email: {}", email);
        UserDto userDto = UserDto.builder()
            .firstName((String) userInfo.getOrDefault("given_name", "Unknown"))
            .lastName((String) userInfo.getOrDefault("family_name", "Unknown"))
            .subscribed(false)
            .build();
        existingUser = userService.create(userDto, email, "null");
      } else {
        //      linkUsers(email, providerName, sub);
      }
      return userMapper.toDto(existingUser);
    } catch (HttpClientErrorException e) {
      log.error("HTTP Error during token exchange: {}", e.getResponseBodyAsString(), e);
      throw new AuthenticationException("Failed to exchange authorization code for tokens.");
    } catch (Exception e) {
      log.error("Error during token exchange or user processing: {}", e.getMessage(), e);
      throw new AuthenticationException("Authentication process failed.");
    }
  }

  /**
   * test sso.
   */
  public void linkUsers(String email, String secondaryProviderName,
      String secondaryProviderUserId) {
    try {
      log.info("Linking user accounts for email: {}", email);
      String userPoolId = "eu-north-1_aD0ST9R4U";
      String primaryUserSub = getUserSubByEmail(userPoolId, email);
      if (primaryUserSub == null) {
        log.error("No primary user found for email: {}", email);
        throw new RuntimeException("Primary user not found for email: " + email);
      }

      if (isUserAlreadyLinked(userPoolId, secondaryProviderName, secondaryProviderUserId)) {
        log.warn("Secondary user is already linked to another primary user.");
        return;
      }

      ProviderUserIdentifierType destinationUser = new ProviderUserIdentifierType()
          .withProviderName("Cognito")
          .withProviderAttributeName("Cognito_Subject")
          .withProviderAttributeValue(primaryUserSub);

      ProviderUserIdentifierType sourceUser = new ProviderUserIdentifierType()
          .withProviderName(secondaryProviderName)
          .withProviderAttributeName("Cognito_Subject")
          .withProviderAttributeValue(secondaryProviderUserId);

      AdminLinkProviderForUserRequest linkRequest = new AdminLinkProviderForUserRequest()
          .withUserPoolId(userPoolId)
          .withDestinationUser(destinationUser)
          .withSourceUser(sourceUser);

      cognitoIdentityProvider.adminLinkProviderForUser(linkRequest);
      log.info("Users linked successfully: {} -> {}", secondaryProviderUserId, primaryUserSub);
    } catch (Exception e) {
      log.error("Error during user linking: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to link users.", e);
    }
  }


  private boolean isUserAlreadyLinked(String userPoolId, String providerName,
      String providerUserId) {
    try {
      AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
          .withUserPoolId(userPoolId)
          .withUsername(providerUserId);

      AdminGetUserResult getUserResult = cognitoIdentityProvider.adminGetUser(getUserRequest);

      return getUserResult.getUserAttributes().stream()
          .anyMatch(attr -> "cognito:linked_accounts".equals(attr.getName()));
    } catch (Exception e) {
      log.error("Error checking if user is already linked: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * test sso.
   */
  private String getUserSubByEmail(String userPoolId, String email) {
    try {
      AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
          .withUserPoolId(userPoolId)
          .withUsername(email);

      AdminGetUserResult getUserResult = cognitoIdentityProvider.adminGetUser(getUserRequest);

      return getUserResult.getUserAttributes().stream()
          .filter(attr -> "sub".equals(attr.getName()))
          .map(AttributeType::getValue)
          .findFirst()
          .orElse(null);
    } catch (Exception e) {
      log.error("Error fetching user by email from Cognito: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to fetch user by email from Cognito.", e);
    }
  }

  /**
   * test sso.
   */
  public static Map<String, String> parseTokens(String tokenResponse) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, String> tokenMap = objectMapper.readValue(tokenResponse, new TypeReference<>() {
      });

      if (!tokenMap.containsKey("access_token") || !tokenMap.containsKey("id_token")) {
        throw new InvalidTokenException("Missing required tokens in response");
      }

      return tokenMap;
    } catch (Exception e) {
      throw new InvalidTokenException("Failed to parse tokens from response");
    }
  }

  /**
   * test sso.
   */
  public static Map<String, Object> decodeIdToken(String idToken) {
    try {
      JWTClaimsSet claimsSet = parseToken(idToken);

      return claimsSet.getClaims();
    } catch (Exception e) {
      throw new InvalidTokenException("Failed to decode ID token.");
    }
  }

}