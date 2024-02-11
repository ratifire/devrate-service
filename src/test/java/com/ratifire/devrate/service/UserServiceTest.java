package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the UserService class. This class tests the behavior of the UserService methods.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  /**
   * Initializes the test environment for the RegistrationControllerTest class. This method is
   * executed before each test method in the class.
   */
  @BeforeEach
  public void init() {
    testUser = User.builder()
        .email("test@gmail.com")
        .firstName("Test first name")
        .lastName("Test last name")
        .country("Test country")
        .isVerified(true)
        .isSubscribed(true)
        .createdAt(LocalDateTime.now())
        .build();
  }

  /**
   * Test method for saving a user entity into the database. This method verifies that the
   * UserService correctly saves a user entity into the database.
   */
  @Test
  public void testSaveUser() {
    when(userRepository.save(any())).thenReturn(testUser);
    User expectedUser = userService.save(User.builder().build());
    assertEquals(expectedUser, testUser);
  }
}
