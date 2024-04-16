package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserSecurityNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserSecurityRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

/**
 * Test class for the {@link UserSecurityService}.
 *
 * <p>Unit tests for the UserService class. This class tests the behavior of the UserService
 * methods.
 */
@ExtendWith(MockitoExtension.class)
public class UserSecurityServiceTest {

  @Mock
  private UserSecurityRepository userSecurityRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private DataMapper<UserDto, User> userMapper;

  @InjectMocks
  private UserSecurityService userSecurityService;

  private final long testId = 123;

  /**
   * Unit test for {@link UserSecurityService#isExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user exists. This method
   * verifies that the RegistrationService correctly returns true when a user with the specified
   * email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsTrue() {
    String existingEmail = "existing@example.com";
    when(userSecurityRepository.existsByEmail(any())).thenReturn(true);
    boolean isExist = userSecurityService.isExistByEmail(existingEmail);
    assertTrue(isExist);
  }

  /**
   * Unit test for {@link UserSecurityService#isExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user does not exist. This
   * method verifies that the RegistrationService correctly returns false when no user with the
   * specified email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsFalse() {
    String notExistingEmail = "notexisting@example.com";
    when(userSecurityRepository.existsByEmail(any())).thenReturn(false);
    boolean isExist = userSecurityService.isExistByEmail(notExistingEmail);
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

    when(userSecurityRepository.save(any())).thenReturn(testUserSecurity);
    UserSecurity expectedUserSecurity = userSecurityService.save(UserSecurity.builder().build());
    assertEquals(expectedUserSecurity, testUserSecurity);
  }

  @Test
  public void testGetById() {
    UserSecurity userSecurity = UserSecurity.builder()
        .id(testId)
        .build();

    when(userSecurityRepository.findById(anyLong())).thenReturn(Optional.of(userSecurity));

    UserSecurity result = userSecurityService.getById(testId);

    assertEquals(testId, result.getId());
    verify(userSecurityRepository, times(1)).findById(testId);
  }

  @Test
  public void testGetById_UserSecurityNotFound() {
    when(userSecurityRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserSecurityNotFoundException.class, () -> userSecurityService.getById(testId));
    verify(userSecurityRepository, times(1)).findById(testId);
  }

  @Test
  public void testGetByUserId() {
    User user = User.builder().id(testId).build();
    UserSecurity security = UserSecurity.builder().user(user).build();

    when(userSecurityRepository.findByUserId(anyLong())).thenReturn(Optional.of(security));

    UserSecurity result = userSecurityService.getByUserId(testId);

    assertEquals(testId, result.getUser().getId());
    verify(userSecurityRepository, times(1)).findByUserId(testId);
  }

  @Test
  public void testGetByUserId_UserSecurityNotFound() {
    when(userSecurityRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

    assertThrows(UserSecurityNotFoundException.class,
        () -> userSecurityService.getByUserId(testId));
    verify(userSecurityRepository, times(1)).findByUserId(testId);
  }

  @Test
  public void testFindByEmail() {
    String email = "test@example.com";
    UserSecurity userSecurity = UserSecurity.builder()
        .email(email)
        .build();

    when(userSecurityRepository.findByEmail(any())).thenReturn(Optional.of(userSecurity));

    UserSecurity result = userSecurityService.findByEmail(email);

    assertEquals(email, result.getEmail());
    verify(userSecurityRepository, times(1)).findByEmail(email);
  }

  @Test
  void authenticate() {
    String email = "test@example.com";
    String password = "password";
    LoginDto loginDto = LoginDto.builder()
        .email(email)
        .password(password)
        .build();

    User user = User.builder().build();
    UserDto expectedDto = UserDto.builder().build();

    UserSecurity userSecurity = UserSecurity.builder()
        .email(email)
        .password(password)
        .user(user)
        .build();

    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(userSecurityRepository.findByEmail(any())).thenReturn(Optional.of(userSecurity));
    when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

    UserDto resultDto = userSecurityService.authenticate(loginDto);

    assertEquals(expectedDto, resultDto);
  }
}
