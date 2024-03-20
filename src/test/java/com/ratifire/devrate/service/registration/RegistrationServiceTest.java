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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.EmailConfirmationCodeException;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.mapper.UserMapper;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.userinfo.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
  private RoleService roleService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Mock
  private EmailService emailService;

  @Mock
  private UserInfoService userInfoService;

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
   * Unit test for {@link RegistrationService#registerUser(UserDto)}.
   *
   * <p>Test case to verify successful user registration. A valid email and password are provided.
   * The user should be successfully registered.
   */
  @Test
  public void testRegisterUser_SuccessfulRegistration() {
    String testEmail = "test@gmail.com";
    String testPassword = "somepassword";

    UserDto testUserDto = UserDto.builder()
        .email(testEmail)
        .password(testPassword)
        .build();

    User testUser = User.builder()
        .email("test@gmail.com")
        .build();

    Role testRole = Role.builder()
        .id(1L)
        .build();

    when(roleService.getDefaultRole()).thenReturn(testRole);
    when(userService.save(any())).thenReturn(testUser);
    when(userMapper.toEntity(any(), any())).thenReturn(testUser);
    when(userMapper.toDto(any())).thenReturn(testUserDto);
    when(userInfoService.create(any(UserInfoDto.class))).thenReturn(UserInfoDto.builder().build());

    when(registrationService.isUserExistByEmail(any())).thenReturn(false);
    when(emailConfirmationCodeService.save(anyLong()))
        .thenReturn(EmailConfirmationCode.builder().build());
    doNothing().when(emailService).sendEmail(any(), anyBoolean());

    UserDto expected = registrationService.registerUser(testUserDto);

    assertEquals(expected, testUserDto);
    verify(emailConfirmationCodeService, times(1)).save(anyLong());
    verify(userInfoService, times(1)).create(any(UserInfoDto.class));
    verify(emailService, times(1)).sendEmail(any(), anyBoolean());
  }

  /**
   * Unit test for {@link RegistrationService#registerUser(UserDto)}.
   *
   * <p>Test case to verify successful user registration. A not valid email and password are
   * provided. UserAlreadyExistException have to be thrown.
   */
  @Test
  public void testRegisterUser_ThrowUserAlreadyExistException() {
    UserDto testUserDto = UserDto.builder()
        .email("test@gmail.com")
        .build();

    Mockito.when(registrationService.isUserExistByEmail(any())).thenReturn(true);

    assertThrows(UserAlreadyExistException.class,
        () -> registrationService.registerUser(testUserDto));
  }

  @Test
  public void testIsCodeConfirmed_Success() {
    long userId = 1L;
    String code = "123456";

    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(code)
        .build();
    when(emailConfirmationCodeService.getEmailConfirmationCodeByUserId(userId))
        .thenReturn(emailConfirmationCode);

    User user = new User();
    when(userService.getById(userId)).thenReturn(user);
    when(userService.save(user)).thenReturn(null);

    doNothing().when(emailConfirmationCodeService).deleteConfirmedCode(anyLong());

    boolean result = registrationService.isCodeConfirmed(userId, code);

    assertTrue(result);
    assertTrue(user.isVerified());
    verify(emailConfirmationCodeService).getEmailConfirmationCodeByUserId(userId);
    verify(userService).getById(userId);
    verify(userService).save(user);
    verify(emailConfirmationCodeService).deleteConfirmedCode(anyLong());
  }

  @Test
  public void testIsCodeConfirmed_InvalidCode() {
    long userId = 1L;
    String code = "123456";
    String wrongCode = "654321";

    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
            .code(wrongCode).build();
    when(emailConfirmationCodeService.getEmailConfirmationCodeByUserId(userId))
        .thenReturn(emailConfirmationCode);

    assertThrows(EmailConfirmationCodeException.class,
        () -> registrationService.isCodeConfirmed(userId, code));
    verify(emailConfirmationCodeService).getEmailConfirmationCodeByUserId(userId);
    verifyNoInteractions(userService, userInfoService);
  }

  @Test
  public void testIsCodeConfirmed_RequestEmptyCode() {
    assertThrows(IllegalArgumentException.class,
        () -> registrationService.isCodeConfirmed(1L, ""));
  }

  @Test
  public void testIsCodeConfirmed_EmptyEmailConfirmationCode() {
    when(emailConfirmationCodeService.getEmailConfirmationCodeByUserId(anyLong()))
        .thenReturn(new EmailConfirmationCode());

    assertThrows(EmailConfirmationCodeException.class,
        () -> registrationService.isCodeConfirmed(1L, "123456"));
  }
}
