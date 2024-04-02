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
import com.ratifire.devrate.repository.UserInfoRepository;
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
  private UserInfoService userInfoService;

  @Mock
  private DataMapper<UserDto, User> userInfoMapper;

  @Mock
  private UserInfoRepository userInfoRepository;

  private final long userId = 1L;

  @Test
  void whenFindById_UserNotFound_ThrowUserInfoNotFoundException() {
    when(userInfoRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userInfoService.findById(userId));
  }

  @Test
  void whenCreate_UserInfoAlreadyExists_ThrowUserInfoAlreadyExistsException() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    when(userInfoRepository.existsById(userId)).thenReturn(true);

    assertThrows(UserInfoAlreadyExistsException.class, () -> userInfoService.create(userDto));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUserInfoDto() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    User user = new User();

    when(userInfoRepository.existsById(userId)).thenReturn(false);
    when(userInfoMapper.toEntity(userDto)).thenReturn(user);
    when(userInfoRepository.save(user)).thenReturn(user);
    when(userInfoMapper.toDto(user)).thenReturn(userDto);

    UserDto createdUserDto = userInfoService.create(userDto);

    assertEquals(userDto.getUserId(), createdUserDto.getUserId());
    verify(userInfoRepository).existsById(userId);
    verify(userInfoRepository).save(user);
    verify(userInfoMapper).toEntity(userDto);
    verify(userInfoMapper).toDto(user);
  }

  @Test
  void whenUpdate_UserInfoNotFound_ThrowUserInfoNotFoundException() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    when(userInfoRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userInfoService.update(userDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserInfoDto() {
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    User user = new User();

    when(userInfoRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userInfoMapper.updateEntity(any(), any())).thenReturn(user);
    when(userInfoRepository.save(user)).thenReturn(user);
    when(userInfoMapper.toDto(user)).thenReturn(userDto);

    UserDto updatedUserDto = userInfoService.update(userDto);

    assertEquals(userDto.getUserId(), updatedUserDto.getUserId());
    verify(userInfoRepository).findById(userId);
    verify(userInfoRepository).save(user);
    verify(userInfoMapper).updateEntity(userDto, user);
    verify(userInfoMapper).toDto(user);
  }

  @Test
  void whenDelete_UserInfoNotFound_ThrowUserInfoNotFoundException() {
    when(userInfoRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userInfoService.delete(userId));
  }
}