package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserSecurityNotFoundException;
import com.ratifire.devrate.repository.UserSecurityRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link UserSecurityService}.
 */
@ExtendWith(MockitoExtension.class)
public class UserSecurityServiceTest {

  @Mock
  private UserSecurityRepository userSecurityRepository;

  @InjectMocks
  private UserSecurityService userSecurityService;

  private final long testId = 123;

  @Test
  public void testUserExistsByEmail_ReturnsFalse() {
    String notExistingEmail = "notexisting@example.com";
    when(userSecurityRepository.existsByEmail(any())).thenReturn(false);
    boolean isExist = userSecurityService.isExistByEmail(notExistingEmail);
    assertFalse(isExist);
  }

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
}
