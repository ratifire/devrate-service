package com.ratifire.devrate.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the UserService class.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private DataMapper<UserDto, User> userMapper;

  @Mock
  private UserRepository userRepository;

  private final long userId = 1L;
  private User testUser;
  private UserDto testUserDto;

  /**
   * Setup method executed before each test method.
   */
  @BeforeEach
  public void init() {
    testUser = User.builder()
        .id(userId)
        .build();

    testUserDto = UserDto.builder()
        .id(userId)
        .build();
  }


  @Test
  void findUserById_UserExists_ReturnsUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto foundUser = userService.findById(userId);

    assertEquals(foundUser, testUserDto);
  }

  @Test
  void findUserById_UserNotFound_ThrowsUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUser() {
    when(userMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);

    User createdUser = userService.create(testUserDto);

    assertEquals(testUserDto.getId(), createdUser.getId());
    verify(userRepository).save(testUser);
    verify(userMapper).toEntity(testUserDto);
  }

  @Test
  void whenUpdate_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.update(testUserDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(userMapper.updateEntity(any(UserDto.class), any(User.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);
    when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto updatedUserDto = userService.update(testUserDto);

    assertEquals(testUserDto.getId(), updatedUserDto.getId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(testUser);
    verify(userMapper).updateEntity(testUserDto, testUser);
    verify(userMapper).toDto(testUser);
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUser() {
    when(userRepository.save(any())).thenReturn(testUser);

    User updatedUser = userService.updateUser(testUser);

    assertEquals(userId, updatedUser.getId());
    verify(userRepository, times(1)).save(testUser);
  }

  @Test
  void whenDelete_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }

  @Test
  void delete_UserExists_DeletesUser() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

    userService.delete(userId);

    verify(userRepository, times(1)).delete(testUser);
  }
}