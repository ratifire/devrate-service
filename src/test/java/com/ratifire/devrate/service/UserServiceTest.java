package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link UserService}.
 *
 * <p>Unit tests for the UserService class. This class tests the behavior of the UserService
 * methods.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  /**
   * Unit test for {@link UserService#isUserExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user exists. This method
   * verifies that the RegistrationService correctly returns true when a user with the specified
   * email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsTrue() {
    String existingEmail = "existing@example.com";
    when(userRepository.existsByEmail(any())).thenReturn(true);
    boolean isExist = userService.isUserExistByEmail(existingEmail);
    assertTrue(isExist);
  }

  /**
   * Unit test for {@link UserService#isUserExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user does not exist. This
   * method verifies that the RegistrationService correctly returns false when no user with the
   * specified email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsFalse() {
    String notExistingEmail = "notexisting@example.com";
    when(userRepository.existsByEmail(any())).thenReturn(false);
    boolean isExist = userService.isUserExistByEmail(notExistingEmail);
    assertFalse(isExist);
  }

  /**
   * Unit test for {@link UserService#save(User)}.
   *
   * <p>Test method for saving a user entity into the database. This method verifies that the
   * UserService correctly saves a user entity into the database.
   */
  @Test
  public void testSaveUser() {
    User testUser = User.builder()
        .email("test@gmail.com")
        .firstName("Test first name")
        .lastName("Test last name")
        .country("Test country")
        .verified(true)
        .subscribed(true)
        .createdAt(LocalDateTime.now())
        .build();

    when(userRepository.save(any())).thenReturn(testUser);
    User expectedUser = userService.save(User.builder().build());
    assertEquals(expectedUser, testUser);
  }
}
