package com.ratifire.devrate.service.userinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.UserInfo;
import com.ratifire.devrate.exception.UserInfoAlreadyExistsException;
import com.ratifire.devrate.exception.UserInfoNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.UserInfoMapper;
import com.ratifire.devrate.repository.UserInfoRepository;
import com.ratifire.devrate.service.UserService;
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
class UserInfoServiceTest {

  @InjectMocks
  private UserInfoService userInfoService;

  @Mock
  private UserInfoMapper userInfoMapper;

  @Mock
  private UserService userService;

  @Mock
  private UserInfoRepository userInfoRepository;

  private final long userId = 1L;

  @Test
  void whenFindByUserId_UserNotFound_ThrowUserInfoNotFoundException() {
    when(userInfoRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userInfoService.findByUserId(userId));
  }

  @Test
  void whenCreate_UserNotExists_ThrowUserNotFoundException() {
    UserInfoDto userInfoDto = UserInfoDto.builder()
        .userId(userId)
        .build();
    when(userService.isUserExistsById(anyLong())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> userInfoService.create(userInfoDto));
  }

  @Test
  void whenCreate_UserInfoAlreadyExists_ThrowUserInfoAlreadyExistsException() {
    UserInfoDto userInfoDto = UserInfoDto.builder()
        .userId(userId)
        .build();
    when(userService.isUserExistsById(anyLong())).thenReturn(true);
    when(userInfoRepository.existsByUserId(userId)).thenReturn(true);

    assertThrows(UserInfoAlreadyExistsException.class, () -> userInfoService.create(userInfoDto));
  }

  @Test
  void whenCreate_Successful_ReturnCreatedUserInfoDto() {
    UserInfoDto userInfoDto = UserInfoDto.builder()
        .userId(userId)
        .build();
    UserInfo userInfo = new UserInfo();

    when(userService.isUserExistsById(anyLong())).thenReturn(true);
    when(userInfoRepository.existsByUserId(userId)).thenReturn(false);
    when(userInfoMapper.toEntity(userInfoDto)).thenReturn(userInfo);
    when(userInfoRepository.save(userInfo)).thenReturn(userInfo);
    when(userInfoMapper.toDto(userInfo)).thenReturn(userInfoDto);

    UserInfoDto createdUserInfoDto = userInfoService.create(userInfoDto);

    assertEquals(userInfoDto.getUserId(), createdUserInfoDto.getUserId());
    verify(userService).isUserExistsById(userId);
    verify(userInfoRepository).existsByUserId(userId);
    verify(userInfoRepository).save(userInfo);
    verify(userInfoMapper).toEntity(userInfoDto);
    verify(userInfoMapper).toDto(userInfo);
  }

  @Test
  void whenUpdate_UserNotExists_ThrowUserNotFoundException() {
    UserInfoDto userInfoDto = UserInfoDto.builder().build();
    when(userService.isUserExistsById(anyLong())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> userInfoService.update(userInfoDto));
  }

  @Test
  void whenUpdate_UserInfoNotFound_ThrowUserInfoNotFoundException() {
    UserInfoDto userInfoDto = UserInfoDto.builder()
        .userId(userId)
        .build();
    when(userService.isUserExistsById(anyLong())).thenReturn(true);
    when(userInfoRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userInfoService.update(userInfoDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserInfoDto() {
    UserInfoDto userInfoDto = UserInfoDto.builder()
        .userId(userId)
        .build();
    UserInfo userInfo = new UserInfo();

    when(userService.isUserExistsById(anyLong())).thenReturn(true);
    when(userInfoRepository.findByUserId(userId)).thenReturn(Optional.of(userInfo));
    doNothing().when(userInfoMapper).updateEntityFromDto(userInfoDto, userInfo);
    when(userInfoRepository.save(userInfo)).thenReturn(userInfo);
    when(userInfoMapper.toDto(userInfo)).thenReturn(userInfoDto);

    UserInfoDto updatedUserInfoDto = userInfoService.update(userInfoDto);

    assertEquals(userInfoDto.getUserId(), updatedUserInfoDto.getUserId());
    verify(userService).isUserExistsById(userId);
    verify(userInfoRepository).findByUserId(userId);
    verify(userInfoRepository).save(userInfo);
    verify(userInfoMapper).updateEntityFromDto(userInfoDto, userInfo);
    verify(userInfoMapper).toDto(userInfo);
  }

  @Test
  void whenDelete_UserNotExists_ThrowUserNotFoundException() {
    when(userService.isUserExistsById(anyLong())).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> userInfoService.delete(userId));
  }

  @Test
  void whenDelete_UserInfoNotFound_ThrowUserInfoNotFoundException() {
    when(userService.isUserExistsById(anyLong())).thenReturn(true);
    when(userInfoRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(UserInfoNotFoundException.class, () -> userInfoService.delete(userId));
  }
}