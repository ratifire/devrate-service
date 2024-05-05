package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.AuthenticationException;
import com.ratifire.devrate.mapper.DataMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;

/**
 * Unit tests for the {@link LoginService} class.
 */
@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

  @Mock
  private UserSecurityService userSecurityService;
  @Mock
  private DataMapper<UserDto, User> userMapper;
  @Mock
  private HttpServletRequest request;

  @InjectMocks
  private LoginService loginService;

  private final String email = "test@example.com";
  private final String password = "password";
  private LoginDto loginDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    loginDto = LoginDto.builder()
        .email(email)
        .password(password)
        .build();
  }

  @Test
  void authenticate_successful() throws ServletException {
    User user = User.builder().build();
    UserDto expectedDto = UserDto.builder().build();

    UserSecurity userSecurity = UserSecurity.builder()
        .email(email)
        .password(password)
        .user(user)
        .build();

    doNothing().when(request).login(anyString(), anyString());
    when(userSecurityService.findByEmail(any())).thenReturn(userSecurity);
    when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

    UserDto resultDto = loginService.authenticate(loginDto, request);

    assertEquals(expectedDto, resultDto);
  }

  @Test
  public void authenticate_disabledUser() throws ServletException {
    doThrow(DisabledException.class).when(request).login(anyString(), anyString());
    assertThrows(DisabledException.class, () -> loginService.authenticate(loginDto, request));
  }

  @Test
  public void authenticate_withBadCredentials() throws ServletException {
    doThrow(AuthenticationException.class).when(request).login(anyString(), anyString());
    assertThrows(AuthenticationException.class, () -> loginService.authenticate(loginDto, request));
  }
}
