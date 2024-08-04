package com.ratifire.devrate.authorization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.authorization.ResourceAuthorizationService;
import com.ratifire.devrate.service.authorization.ResourceOwnerVerifier;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@ExtendWith(MockitoExtension.class)
class ResourceAuthorizationServiceTest {

  @InjectMocks
  private ResourceAuthorizationService resourceAuthorizationService;

  @Mock
  private ResourceOwnerVerifier resourceOwnerVerifier;

  @Mock
  private Map<String, ResourceOwnerVerifier> resourceTypeToOwnerVerifier;

  private UserSecurity userSecurityLoggedUser;
  private User loggedUser;
  private final long loggedUserId = 1L;
  private final long nonLoggedUserId = 2L;
  private final String resourceType = "someResource";
  private final long resourceId = 100L;


  @BeforeEach
  void setUp() {
    loggedUser = User.builder().id(loggedUserId).build();
    userSecurityLoggedUser = UserSecurity.builder().user(loggedUser).build();

    SecurityContextHolder.setContext(new SecurityContextImpl());
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(userSecurityLoggedUser, null));
  }

  @Test
  void isPathUserIdMatchingLoggedUserTest() {
    assertTrue(resourceAuthorizationService.isPathUserIdMatchingLoggedUser(loggedUserId));
    assertFalse(resourceAuthorizationService.isPathUserIdMatchingLoggedUser(nonLoggedUserId));
  }

  @Test
  void isResourceOwnedByLoggedUser_OwnedTest() {
    when(resourceTypeToOwnerVerifier.get(resourceType)).thenReturn(resourceOwnerVerifier);
    when(resourceOwnerVerifier.verifyOwner(anyLong(), anyLong())).thenReturn(true);

    assertTrue(resourceAuthorizationService.isResourceOwnedByLoggedUser(
        resourceType, resourceId));
  }

  @Test
  void isResourceOwnedByLoggedUser_NotOwnedTest() {
    when(resourceTypeToOwnerVerifier.get(resourceType)).thenReturn(resourceOwnerVerifier);
    when(resourceOwnerVerifier.verifyOwner(anyLong(), anyLong())).thenReturn(false);

    assertFalse(resourceAuthorizationService.isResourceOwnedByLoggedUser(
        resourceType, resourceId));
  }
}