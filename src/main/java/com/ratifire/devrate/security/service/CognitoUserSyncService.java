package com.ratifire.devrate.security.service;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_DEFAULT_PROVIDER_NAME;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_PROVIDER_NAME;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ProviderUserIdentifierType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.security.exception.OauthException;
import com.ratifire.devrate.security.model.CognitoUserInfo;
import com.ratifire.devrate.security.model.enums.AccessLevel;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service for synchronizing and linking internal user entities with their corresponding Cognito
 * users.
 */
@Service
@RequiredArgsConstructor
@Profile("!local")
public class CognitoUserSyncService {

  private final CognitoApiClientService cognitoApiClient;

  /**
   * Ensures the given internal user is linked and synchronized with the corresponding Cognito user.
   * If the user is not already linked in Cognito, links the user and updates Cognito user
   * attributes with the latest internal user information.
   *
   * @param internalUser The internal user entity to be linked and synced.
   * @param userInfo     The Cognito user information record.
   * @throws OauthException if the primary Cognito user cannot be found or user linking fails.
   */
  public void ensureInternalUserLinkedAndSyncedWithCognito(
      User internalUser,
      CognitoUserInfo userInfo) {
    final String email = userInfo.email();
    ProviderUserIdentifierType cognitoPrimaryUser = findCognitoPoolPrimaryUser(email)
        .orElseThrow(() -> new OauthException("Primary cognito user not found for email " + email));
    String cognitoPrimaryUserSubject = cognitoPrimaryUser.getProviderAttributeValue();

    if (areCognitoUsersLinked(userInfo.linkedRecord(), userInfo.isPrimaryRecord(),
        cognitoPrimaryUserSubject)) {
      return;
    }
    linkCognitoUsersInPool(cognitoPrimaryUser, userInfo);
    cognitoApiClient.updateCognitoUserAttributes(
        userInfo.subject(),
        internalUser.getId(),
        AccessLevel.getDefaultRole(),
        false,
        cognitoPrimaryUserSubject);
  }

  /**
   * Synchronizes specified attributes with Cognito for all users linked to the given email.
   *
   * @param email      The email address used to identify linked Cognito users.
   * @param attributes A list of attributes to be synchronized with each linked Cognito user.
   */
  public void synchronizeAttributeWithCognitoForAllLinkedUser(String email,
      List<AttributeType> attributes) {
    List<UserType> users = cognitoApiClient.getListCognitoUsersByEmail(email).getUsers();
    for (UserType user : users) {
      synchronizeAttributeWithCognitoForSingleUser(user.getUsername(), attributes);
    }
  }

  /**
   * Synchronizes specified attributes with Cognito for single users linked to the given username.
   *
   * @param username      The username used to identify linked Cognito users.
   * @param attributes A list of attributes to be synchronized with each linked Cognito user.
   */
  public void synchronizeAttributeWithCognitoForSingleUser(String username,
      List<AttributeType> attributes) {
    cognitoApiClient.updateCognitoUserAttributes(attributes, username);
  }

  private boolean areCognitoUsersLinked(String linkedRecord, String isPrimaryRecord,
      String primarySubject) {
    if (StringUtils.isEmpty(linkedRecord)) {
      return false;
    }

    if (linkedRecord.equals(primarySubject)) {
      return true;
    }
    if (Boolean.TRUE.toString().equals(isPrimaryRecord)) {
      return true;
    }
    throw new OauthException(String.format(
        "Cognito pool user linked record (%s) does not match the primary pool user subject (%s)",
        linkedRecord, primarySubject));
  }

  private void linkCognitoUsersInPool(ProviderUserIdentifierType destinationUser,
      CognitoUserInfo userInfo) {
    cognitoApiClient.linkCognitoUsersInPool(destinationUser, userInfo.provider(),
        userInfo.subject());
  }

  private Optional<ProviderUserIdentifierType> findCognitoPoolPrimaryUser(String email) {
    return cognitoApiClient.getListCognitoUsersByEmail(email).getUsers().stream()
        .filter(this::isPrimary)
        .findFirst()
        .map(this::toProviderIdentifier);
  }

  private boolean isPrimary(UserType user) {
    return user.getAttributes().stream()
        .anyMatch(attr -> ATTRIBUTE_IS_PRIMARY_RECORD.equals(attr.getName())
            && Boolean.TRUE.toString().equals(attr.getValue()));
  }

  private ProviderUserIdentifierType toProviderIdentifier(UserType user) {
    ProviderUserIdentifierType identifier = new ProviderUserIdentifierType();
    identifier.setProviderName(getProviderNameAttribute(user.getAttributes()));
    identifier.setProviderAttributeValue(user.getUsername());
    return identifier;
  }

  private String getProviderNameAttribute(List<AttributeType> attributes) {
    return attributes.stream()
        .filter(attribute -> StringUtils.equals(attribute.getName(),
            ATTRIBUTE_PROVIDER_NAME))
        .map(AttributeType::getValue)
        .findFirst()
        .orElse(ATTRIBUTE_DEFAULT_PROVIDER_NAME);
  }

}