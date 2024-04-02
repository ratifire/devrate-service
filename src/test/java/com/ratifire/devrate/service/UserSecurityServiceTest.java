package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link UserSecurityService}.
 *
 * <p>Unit tests for the UserService class. This class tests the behavior of the UserService
 * methods.
 */
@ExtendWith(MockitoExtension.class)
public class UserSecurityServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserSecurityService userSecurityService;

  /**
   * Unit test for {@link UserSecurityService#isUserExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user exists. This method
   * verifies that the RegistrationService correctly returns true when a user with the specified
   * email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsTrue() {
    String existingEmail = "existing@example.com";
    when(userRepository.existsByEmail(any())).thenReturn(true);
    boolean isExist = userSecurityService.isUserExistByEmail(existingEmail);
    assertTrue(isExist);
  }

  /**
   * Unit test for {@link UserSecurityService#isUserExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user does not exist. This
   * method verifies that the RegistrationService correctly returns false when no user with the
   * specified email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsFalse() {
    String notExistingEmail = "notexisting@example.com";
    when(userRepository.existsByEmail(any())).thenReturn(false);
    boolean isExist = userSecurityService.isUserExistByEmail(notExistingEmail);
    assertFalse(isExist);
  }

  /**
   * Unit test for {@link UserSecurityService#save(UserSecurity)}.
   *
   * <p>Test method for saving a user entity into the database. This method verifies that the
   * UserService correctly saves a user entity into the database.
   */
  @Test
  public void testSaveUser() {
    UserSecurity testUserSecurity = UserSecurity.builder()
        .email("test@gmail.com")
        .verified(true)
        .createdAt(LocalDateTime.now())
        .build();

    when(userRepository.save(any())).thenReturn(testUserSecurity);
    UserSecurity expectedUserSecurity = userSecurityService.save(UserSecurity.builder().build());
    assertEquals(expectedUserSecurity, testUserSecurity);
  }
}
