package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the RegistrationService class. This class tests the behavior of the
 * RegistrationService methods.
 */
@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private RegistrationService registrationService;

  /**
   * Test method for checking if a user exists by email when the user exists. This method verifies
   * that the RegistrationService correctly returns true when a user with the specified email exists
   * in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsTrue() {
    String existingEmail = "existing@example.com";
    when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(User.builder().build()));
    boolean isExist = registrationService.isUserExistByEmail(existingEmail);
    assertTrue(isExist);
  }

  /**
   * Test method for checking if a user exists by email when the user does not exist. This method
   * verifies that the RegistrationService correctly returns false when no user with the specified
   * email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsFalse() {
    String notExistingEmail = "notexisting@example.com";
    when(userRepository.findByEmail(notExistingEmail)).thenReturn(Optional.empty());
    boolean isExist = registrationService.isUserExistByEmail(notExistingEmail);
    assertFalse(isExist);
  }
}
