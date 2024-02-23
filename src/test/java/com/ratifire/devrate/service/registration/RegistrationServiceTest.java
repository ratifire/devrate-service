package com.ratifire.devrate.service.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.email.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

/**
 * Test class for the {@link RegistrationService}.
 *
 * <p>Unit tests for the RegistrationService class. This class tests the behavior of the
 * RegistrationService methods.
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
public class RegistrationServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private RoleService roleService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Mock
  private EmailService emailService;

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
    String testPassword = "somepassword";

    SignUpDto testSignUpDto = SignUpDto.builder()
        .email("test@gmail.com")
        .password(testPassword)
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
    when(emailConfirmationCodeService.save(anyLong()))
        .thenReturn(EmailConfirmationCode.builder().build());
    doNothing().when(emailService).sendEmail(any(), anyBoolean());
    when(passwordEncoder.encode(any())).thenReturn(testPassword);

    User expectedUser = registrationService.registerUser(testSignUpDto);

    assertEquals(expectedUser, testUser);
    verify(emailConfirmationCodeService, times(1)).save(anyLong());
    verify(emailService, times(1)).sendEmail(any(), anyBoolean());
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
