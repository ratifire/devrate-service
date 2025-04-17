package com.ratifire.devrate.security.migration;

import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_IS_PRIMARY_RECORD;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_LINKED_RECORD_SUBJECT;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_ROLE;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.ATTRIBUTE_USER_ID;
import static com.ratifire.devrate.security.model.constants.CognitoConstant.NONE_VALUE;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.ratifire.devrate.security.service.CognitoApiClientService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CognitoMigrationService {

  private final CognitoApiClientService cognitoApiClient;

  public void migrateAllUsers() {
    String paginationToken = null;

    do {
      ListUsersResult result = cognitoApiClient.getAllListCognitoUsersWithPagination(60,
          paginationToken);
      for (UserType user : result.getUsers()) {
        String username = user.getUsername();
        try {
          migrateUserAttributes(username);
        } catch (Exception e) {
          log.error("Failed to migrate user with username={}: {}", username, e.getMessage());
        }
      }

      paginationToken = result.getPaginationToken();

    } while (paginationToken != null);
  }

  public void migrateUserAttributes(String username) {
    log.info("Migration started for user: {}", username);

    AdminGetUserResult cognitoUserDetails = cognitoApiClient.getCognitoUserDetails(username);
    Map<String, String> oldAttrs = cognitoUserDetails.getUserAttributes().stream()
        .collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));

    String oldUserId = oldAttrs.getOrDefault("custom:userId", null);
    String oldRole = oldAttrs.getOrDefault("custom:role", null);

    if (StringUtils.isEmpty(oldUserId) || StringUtils.isEmpty(oldRole)) {
      log.warn("Skipped: user '{}' is missing required attributes 'custom:userId' or 'custom:role'",
          username);
      return;
    }

    List<AttributeType> newAttributes = List.of(
        new AttributeType().withName(ATTRIBUTE_USER_ID).withValue(oldUserId),
        new AttributeType().withName(ATTRIBUTE_ROLE).withValue(oldRole),
        new AttributeType().withName(ATTRIBUTE_IS_PRIMARY_RECORD)
            .withValue(Boolean.TRUE.toString()),
        new AttributeType().withName(ATTRIBUTE_LINKED_RECORD_SUBJECT).withValue(NONE_VALUE)
    );

    cognitoApiClient.updateCognitoUserAttributes(newAttributes, username);

    log.info("Migration completed successfully for user: {}", username);
  }

}
