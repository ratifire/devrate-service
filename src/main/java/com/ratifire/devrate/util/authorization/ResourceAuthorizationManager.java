package com.ratifire.devrate.util.authorization;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.OwnershipResourceVerifiable;
import com.ratifire.devrate.service.UserSecurityService;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

/**
 * This class manages resource authorization by implementing the {@link AuthorizationManager}
 * interface. It verifies whether a user has ownership over a resource or matches a given user ID.
 */
@Component
public class ResourceAuthorizationManager implements
    AuthorizationManager<RequestAuthorizationContext> {

  private final UserSecurityService userSecurityService;
  private final AuthorizationDecision positiveDecision;
  private final AuthorizationDecision negativeDecision;
  private final Map<String, OwnershipResourceVerifiable> ownershipCheckers;

  /**
   * Constructs a new ResourceAuthorizationManager.
   */
  public ResourceAuthorizationManager(
      @Qualifier("ownershipCheckers") Map<String, OwnershipResourceVerifiable> ownershipCheckers,
      UserSecurityService userSecurityService,
      @Qualifier("positiveAuthorizationDecision") AuthorizationDecision positiveDecision,
      @Qualifier("negativeAuthorizationDecision") AuthorizationDecision negativeDecision) {
    this.userSecurityService = userSecurityService;
    this.ownershipCheckers = ownershipCheckers;
    this.positiveDecision = positiveDecision;
    this.negativeDecision = negativeDecision;
  }

  @Override
  public void verify(Supplier<Authentication> authentication,
      RequestAuthorizationContext context) {
    AuthorizationDecision decision = check(authentication, context);
  }

  @Override
  public AuthorizationDecision check(Supplier<Authentication> auth,
      RequestAuthorizationContext context) {

    Authentication authentication = auth.get();
    Map<String, String> requestVariables = context.getVariables();

    if (requestVariables.containsKey("userId") && isPathUserIdMatchingLoggedUser(authentication,
        requestVariables)) {
      return positiveDecision;
    }

    if (requestVariables.containsKey("id") && requestVariables.containsKey("resourceType")
        && isResourceOwnedByUser(authentication, requestVariables)) {
      return positiveDecision;
    }
    return negativeDecision;
  }

  private boolean isPathUserIdMatchingLoggedUser(Authentication authentication,
      Map<String, String> variables) {
    long pathUserId = Long.parseLong(variables.get("userId"));
    long loggedUserId = getIdLoggedUserFromAuthentication(authentication);
    return loggedUserId == pathUserId;
  }

  private boolean isResourceOwnedByUser(Authentication authentication,
      Map<String, String> variables) {
    long resourceId = Long.parseLong(variables.get("id"));
    long loggedUserId = getIdLoggedUserFromAuthentication(authentication);
    OwnershipResourceVerifiable checker = ownershipCheckers.get(
        variables.get("resourceType"));
    return checker.checkOwnership(resourceId, loggedUserId);
  }

  private long getIdLoggedUserFromAuthentication(Authentication authentication) {
    UserSecurity userSecurity = userSecurityService.findByEmail(authentication.getName());
    return userSecurity.getUser().getId();
  }
}