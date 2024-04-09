package com.ratifire.devrate.service.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.EmailConfirmationCodeExpiredException;
import com.ratifire.devrate.exception.EmailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.UserSecurityAlreadyExistException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.user.UserService;
import com.ratifire.devrate.util.websocket.WebSocketSender;
import java.time.LocalDateTime;
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
  private UserSecurityService userSecurityService;

  @Mock
  private RoleService roleService;

  @Mock
  private DataMapper<UserRegistrationDto, UserSecurity> userMapper;

  @Mock
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Mock
  private EmailService emailService;

  @Mock
  private WebSocketSender webSocketSender;

  @Mock
  private UserService userService;

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
    when(userSecurityService.isExistByEmail(any())).thenReturn(true);
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
    when(userSecurityService.isExistByEmail(any())).thenReturn(false);
    boolean isExist = registrationService.isUserExistByEmail(notExistingEmail);
    assertFalse(isExist);
  }

  /**
   * Unit test for {@link RegistrationService#registerUser(UserRegistrationDto)}.
   *
   * <p>Test case to verify successful user registration. A valid email and password are provided.
   * The user should be successfully registered.
   */
  @Test
  public void testRegisterUser_SuccessfulRegistration() {
    String testEmail = "test@gmail.com";
    String testPassword = "somepassword";

    UserRegistrationDto testUserRegistrationDto = UserRegistrationDto.builder()
        .email(testEmail)
        .password(testPassword)
        .build();

    UserSecurity testUserSecurity = UserSecurity.builder()
        .email("test@gmail.com")
        .build();

    Role testRole = Role.builder()
        .id(1L)
        .build();

    when(roleService.getDefaultRole()).thenReturn(testRole);
    when(userSecurityService.save(any())).thenReturn(testUserSecurity);
    when(userMapper.toEntity(any(UserRegistrationDto.class))).thenReturn(testUserSecurity);
    when(userMapper.toDto(any(UserSecurity.class))).thenReturn(testUserRegistrationDto);
    when(userService.create(any(UserDto.class))).thenReturn(new User());

    when(registrationService.isUserExistByEmail(any())).thenReturn(false);
    when(emailConfirmationCodeService.save(anyLong()))
        .thenReturn(EmailConfirmationCode.builder().build());
    doNothing().when(emailService).sendEmail(any(), anyBoolean());

    UserRegistrationDto expected = registrationService.registerUser(testUserRegistrationDto);

    assertEquals(expected, testUserRegistrationDto);
    verify(emailConfirmationCodeService, times(1)).save(anyLong());
    verify(userService, times(1)).create(any(UserDto.class));
    verify(emailService, times(1)).sendEmail(any(), anyBoolean());
  }

  /**
   * Unit test for {@link RegistrationService#registerUser(UserRegistrationDto)}.
   *
   * <p>Test case to verify successful user registration. A not valid email and password are
   * provided. UserAlreadyExistException have to be thrown.
   */
  @Test
  public void testRegisterUser_ThrowUserAlreadyExistException() {
    UserRegistrationDto testUserRegistrationDto = UserRegistrationDto.builder()
        .email("test@gmail.com")
        .build();

    Mockito.when(registrationService.isUserExistByEmail(any())).thenReturn(true);

    assertThrows(UserSecurityAlreadyExistException.class,
        () -> registrationService.registerUser(testUserRegistrationDto));
  }

  /**
   * Unit test for {@link RegistrationService#confirmRegistration(String)}.
   *
   * <p>This test verifies the behavior of the confirmRegistration method in RegistrationService
   * when a valid confirmation code is provided and the registration is successful.
   */
  @Test
  public void testConfirmRegistration_Success() {
    long userSecurityId = 1L;
    String code = "123456";

    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(code)
        .userSecurityId(userSecurityId)
        .createdAt(LocalDateTime.now())
        .build();
    when(emailConfirmationCodeService.findEmailConfirmationCode(code))
        .thenReturn(emailConfirmationCode);

    UserSecurity userSecurity = new UserSecurity();
    userSecurity.setId(userSecurityId);
    when(userSecurityService.getById(userSecurityId)).thenReturn(userSecurity);
    when(userSecurityService.save(userSecurity)).thenReturn(null);
    doNothing().when(webSocketSender).addGreetingNotification(any());

    doNothing().when(emailConfirmationCodeService).deleteConfirmedCode(anyLong());

    long actualUserId = registrationService.confirmRegistration(code);

    assertEquals(userSecurityId, actualUserId);
    assertTrue(userSecurity.isVerified());
    verify(emailConfirmationCodeService).findEmailConfirmationCode(code);
    verify(userSecurityService).getById(userSecurityId);
    verify(userSecurityService).save(userSecurity);
    verify(emailConfirmationCodeService).deleteConfirmedCode(anyLong());
  }

  /**
   * Unit test for {@link RegistrationService#confirmRegistration(String)}.
   *
   * <p>This test verifies the behavior of the confirmRegistration method in RegistrationService
   * when an empty confirmation code is provided, and it expects an
   * EmailConfirmationCodeRequestException to be thrown.
   */
  @Test
  public void testConfirmRegistration_RequestEmptyCode() {
    assertThrows(EmailConfirmationCodeRequestException.class,
        () -> registrationService.confirmRegistration(""));
  }

  /**
   * Unit test for {@link RegistrationService#confirmRegistration(String)}.
   *
   * <p>Tests the confirmation of registration with an expired confirmation code.
   * Verifies that an {@link EmailConfirmationCodeExpiredException} is thrown
   * and that neither the {@link UserSecurityService} nor the {@link EmailConfirmationCodeService}
   * are invoked for further actions.
   */
  @Test
  void testConfirmRegistrationExpiredCode() {
    String code = "123456";
    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(code)
        .userSecurityId(1L)
        .createdAt(LocalDateTime.now().minusHours(25))
        .build();
    when(emailConfirmationCodeService.findEmailConfirmationCode(code))
        .thenReturn(emailConfirmationCode);

    assertThrows(EmailConfirmationCodeExpiredException.class,
        () -> registrationService.confirmRegistration(code));

    verify(userSecurityService, never()).getById(anyLong());
    verify(emailConfirmationCodeService, times(1)).deleteConfirmedCode(anyLong());
  }
}
