package com.ratifire.devrate.service.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.entity.EmailConfirmationCode;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.MailConfirmationCodeRequestException;
import com.ratifire.devrate.exception.UserSecurityAlreadyExistException;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import com.ratifire.devrate.service.user.UserService;
import java.time.LocalDateTime;
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
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
public class RegistrationServiceTest {

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private RoleService roleService;

  @Mock
  private EmailConfirmationCodeService emailConfirmationCodeService;

  @Mock
  private EmailService emailService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private UserService userService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private RegistrationService registrationService;

  @Test
  public void testRegisterUser_SuccessfulRegistration() {
    String testEmail = "test@gmail.com";
    String testPassword = "somepassword";
    String confirmationCode = "123456";

    UserSecurity testUserSecurity = UserSecurity.builder()
        .email("test@gmail.com")
        .build();

    Role testRole = Role.builder()
        .id(1L)
        .build();

    when(registrationService.isUserExistByEmail(any())).thenReturn(false);
    when(userService.create(any(UserDto.class))).thenReturn(new User());
    when(passwordEncoder.encode(any())).thenReturn(testPassword);
    when(roleService.getDefaultRole()).thenReturn(testRole);
    when(userSecurityService.save(any())).thenReturn(testUserSecurity);
    when(emailConfirmationCodeService.createConfirmationCode(anyLong()))
        .thenReturn(confirmationCode);
    doNothing().when(emailService).sendConfirmationCodeEmail(any(), any());

    UserRegistrationDto testUserRegistrationDto = UserRegistrationDto.builder()
        .email(testEmail)
        .password(testPassword)
        .build();

    registrationService.registerUser(testUserRegistrationDto);

    verify(emailConfirmationCodeService, times(1))
        .createConfirmationCode(anyLong());
    verify(userService, times(1)).create(any(UserDto.class));
    verify(emailService, times(1)).sendConfirmationCodeEmail(any(), any());
  }

  @Test
  public void testRegisterUser_ThrowUserAlreadyExistException() {
    UserRegistrationDto testUserRegistrationDto = UserRegistrationDto.builder()
        .email("test@gmail.com")
        .build();

    Mockito.when(registrationService.isUserExistByEmail(any())).thenReturn(true);

    assertThrows(UserSecurityAlreadyExistException.class,
        () -> registrationService.registerUser(testUserRegistrationDto));
  }

  @Test
  public void testConfirmRegistration_Success() {
    long userSecurityId = 1L;
    String code = "123456";

    EmailConfirmationCode emailConfirmationCode = EmailConfirmationCode.builder()
        .code(code)
        .userSecurityId(userSecurityId)
        .createdAt(LocalDateTime.now())
        .build();

    User testUser = User.builder()
        .id(1L)
        .build();
    UserSecurity userSecurity = UserSecurity.builder()
        .id(userSecurityId)
        .user(testUser)
        .build();

    when(emailConfirmationCodeService.findEmailConfirmationCode(code))
        .thenReturn(emailConfirmationCode);
    when(userSecurityService.getById(userSecurityId)).thenReturn(userSecurity);
    when(userSecurityService.save(userSecurity)).thenReturn(null);
    doNothing().when(emailConfirmationCodeService).deleteConfirmedCode(anyLong());
    doNothing().when(notificationService).addGreetingNotification(any());
    doNothing().when(emailService).sendGreetingsEmail(any(), any());

    long actualUserId = registrationService.confirmRegistration(code);

    assertEquals(userSecurityId, actualUserId);
    assertTrue(userSecurity.isVerified());
    verify(emailConfirmationCodeService).findEmailConfirmationCode(code);
    verify(userSecurityService).getById(userSecurityId);
    verify(userSecurityService).save(userSecurity);
    verify(emailConfirmationCodeService).deleteConfirmedCode(anyLong());
  }

  @Test
  public void testConfirmRegistration_RequestEmptyCode() {
    assertThrows(MailConfirmationCodeRequestException.class,
        () -> registrationService.confirmRegistration(""));
  }
}
