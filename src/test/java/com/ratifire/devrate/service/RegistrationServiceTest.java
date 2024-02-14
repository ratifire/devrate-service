package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link RegistrationService}.
 *
 * <p>Unit tests for the RegistrationService class. This class tests the behavior of the
 * RegistrationService methods.
 */
@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private RoleService roleService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private RegistrationService registrationService;

  /**
   * Unit test for {@link RegistrationService#isUserExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user exists. This method
   * verifies that the RegistrationService correctly returns true when a user with the specified
   * email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsTrue() {
    String existingEmail = "existing@example.com";
    when(userService.isUserExistByEmail(any())).thenReturn(true);
    boolean isExist = registrationService.isUserExistByEmail(existingEmail);
    assertTrue(isExist);
  }

  /**
   * Unit test for {@link RegistrationService#isUserExistByEmail(String)}.
   *
   * <p>Test method for checking if a user exists by email when the user does not exist. This
   * method verifies that the RegistrationService correctly returns false when no user with the
   * specified email exists in the database.
   */
  @Test
  public void testUserExistsByEmail_ReturnsFalse() {
    String notExistingEmail = "notexisting@example.com";
    when(userService.isUserExistByEmail(any())).thenReturn(false);
    boolean isExist = registrationService.isUserExistByEmail(notExistingEmail);
    assertFalse(isExist);
  }

  /**
   * Unit test for {@link RegistrationService#registerUser(SignUpDto)}.
   *
   * <p>Test case to verify successful user registration. A valid email and password are provided.
   * The user should be successfully registered.
   */
  @Test
  public void testRegisterUser_SuccessfulRegistration() {
    SignUpDto testSignUpDto = SignUpDto.builder()
        .email("test@gmail.com")
        .build();

    User testUser = User.builder()
        .email("test@gmail.com")
        .build();

    Role testRole = Role.builder()
        .id(1L)
        .build();

    when(registrationService.isUserExistByEmail(any())).thenReturn(false);
    when(userMapper.toEntity(any())).thenReturn(testUser);
    when(userService.save(any())).thenReturn(testUser);
    when(roleService.getRoleByName(any())).thenReturn(testRole);
    when(userSecurityService.save(any())).thenReturn(UserSecurity.builder().build());
    User expectedUser = registrationService.registerUser(testSignUpDto);
    assertEquals(expectedUser, testUser);
  }

  /**
   * Unit test for {@link RegistrationService#registerUser(SignUpDto)}.
   *
   * <p>Test case to verify successful user registration. A not valid email and password are
   * provided. UserAlreadyExistException have to be thrown.
   */
  @Test
  public void testRegisterUser_ThrowUserAlreadyExistException() {
    SignUpDto testSignUpDto = SignUpDto.builder()
        .email("test@gmail.com")
        .build();

    Mockito.when(registrationService.isUserExistByEmail(any())).thenReturn(true);

    assertThrows(UserAlreadyExistException.class,
        () -> registrationService.registerUser(testSignUpDto));
  }
}
