package com.ratifire.devrate.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.Optional;
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

  @Test
  void whenFindById_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUser() {
    UserDto userDto = UserDto.builder()
        .id(userId)
        .build();
    User user = User.builder()
        .id(userId)
        .build();

    when(userMapper.toEntity(userDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);

    User createdUser = userService.create(userDto);

    assertEquals(userDto.getId(), createdUser.getId());
    verify(userRepository).save(user);
    verify(userMapper).toEntity(userDto);
  }

  @Test
  void whenUpdate_UserNotFound_ThrowUserNotFoundException() {
    UserDto userDto = UserDto.builder()
        .id(userId)
        .build();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.update(userDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserDto() {
    UserDto userDto = UserDto.builder()
        .id(userId)
        .build();
    User user = new User();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.updateEntity(any(), any())).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    UserDto updatedUserDto = userService.update(userDto);

    assertEquals(userDto.getId(), updatedUserDto.getId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
    verify(userMapper).updateEntity(userDto, user);
    verify(userMapper).toDto(user);
  }

  @Test
  void whenDelete_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }
}