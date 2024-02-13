package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.UserSecurityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link UserSecurityService}.
 * <p>
 * This class contains unit tests for the UserSecurityService using Mockito. It tests the
 * security-related functionality such as authentication and authorization.
 */
@ExtendWith(MockitoExtension.class)
public class UserSecurityServiceTest {

  @Mock
  private UserSecurityRepository userSecurityRepository;

  @InjectMocks
  private UserSecurityService userSecurityService;

  /**
   * Unit test for {@link UserSecurityService#save(UserSecurity)}.
   * <p>
   * Test case to verify successful user security data persistence.
   *
   * <p>Scenario:
   * - A user security object is provided. - The user security data should be saved successfully.
   */
  @Test
  public void testSaveUserSecurity_Successful() {
    UserSecurity testUserSecurity = UserSecurity.builder()
        .password("password@example.com")
        .build();

    when(userSecurityRepository.save(any())).thenReturn(testUserSecurity);
    UserSecurity expected = userSecurityService.save(UserSecurity.builder().build());
    assertEquals(expected, testUserSecurity);
  }
}
