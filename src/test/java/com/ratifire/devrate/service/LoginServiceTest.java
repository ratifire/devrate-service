package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LoginDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.mapper.DataMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

/**
 * Unit tests for the {@link LoginService} class.
 */
@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

  @Mock
  private Authentication authentication;

  @Mock
  private UserSecurityService userSecurityService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private DataMapper<UserDto, User> userMapper;

  @InjectMocks
  private LoginService loginService;

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
    when(userSecurityService.findByEmail(any())).thenReturn(userSecurity);
    when(userMapper.toDto(any(User.class))).thenReturn(expectedDto);

    UserDto resultDto = loginService.authenticate(loginDto);

    assertEquals(expectedDto, resultDto);
  }
}
