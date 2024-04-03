package com.ratifire.devrate.service.userinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.UserInfoAlreadyExistsException;
import com.ratifire.devrate.exception.UserInfoNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the UserInfoService class.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private DataMapper<UserDto, User> userInfoMapper;

  @Mock
  private UserRepository userRepository;

  private final long userId = 1L;

  @Test
  void whenFindById_UserNotFound_ThrowUserInfoNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userService.findById(userId));
  }

  @Test
  void whenCreate_UserInfoAlreadyExists_ThrowUserInfoAlreadyExistsException() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    when(userRepository.existsById(userId)).thenReturn(true);

    assertThrows(UserInfoAlreadyExistsException.class, () -> userService.create(userDto));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUserInfoDto() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    User user = new User();

    when(userRepository.existsById(userId)).thenReturn(false);
    when(userInfoMapper.toEntity(userDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userInfoMapper.toDto(user)).thenReturn(userDto);

    UserDto createdUserDto = userService.create(userDto);

    assertEquals(userDto.getUserId(), createdUserDto.getUserId());
    verify(userRepository).existsById(userId);
    verify(userRepository).save(user);
    verify(userInfoMapper).toEntity(userDto);
    verify(userInfoMapper).toDto(user);
  }

  @Test
  void whenUpdate_UserInfoNotFound_ThrowUserInfoNotFoundException() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userService.update(userDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserInfoDto() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    User user = new User();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userInfoMapper.updateEntity(any(), any())).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userInfoMapper.toDto(user)).thenReturn(userDto);

    UserDto updatedUserDto = userService.update(userDto);

    assertEquals(userDto.getUserId(), updatedUserDto.getUserId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
    verify(userInfoMapper).updateEntity(userDto, user);
    verify(userInfoMapper).toDto(user);
  }

  @Test
  void whenDelete_UserInfoNotFound_ThrowUserInfoNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userService.delete(userId));
  }
}