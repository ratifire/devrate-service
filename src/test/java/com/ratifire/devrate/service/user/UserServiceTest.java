package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.enums.LanguageProficiencyLevel.INTERMEDIATE_B1;
import static com.ratifire.devrate.enums.LanguageProficiencyName.ENGLISH;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.exception.LanguageProficiencyAlreadyExistException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
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
  private DataMapper dataMapper;

  @Mock
  private UserRepository userRepository;

  private long userId = 1L;

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

    when(dataMapper.toEntity(userDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);

    User createdUser = userService.create(userDto);

    assertEquals(userDto.getId(), createdUser.getId());
    verify(userRepository).save(user);
    verify(dataMapper).toEntity(userDto);
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
    when(dataMapper.updateEntity(any(), any())).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(dataMapper.toDto(user)).thenReturn(userDto);

    UserDto updatedUserDto = userService.update(userDto);

    assertEquals(userDto.getId(), updatedUserDto.getId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
    verify(dataMapper).updateEntity(userDto, user);
    verify(dataMapper).toDto(user);
  }

  @Test
  void whenDelete_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }

  @Test
  void findAllLanguageProficienciesByUserIdTest() {
    long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setLanguageProficiency(new ArrayList<>());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    List<LanguageProficiencyDto> result = userService.findAllLanguageProficienciesByUserId(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userRepository, times(1)).findById(userId);
  }

  @Test
  void findAllLanguageProficienciesByUserIdThrowsUserNotFoundException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.findAllLanguageProficienciesByUserId(userId));
  }

  @Test
  void createLanguageProficiencyTest() {
    long userId = 1L;
    User user = new User();
    user.setId(userId);
    user.setLanguageProficiency(new ArrayList<>());
    LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
    LanguageProficiency languageProficiency = new LanguageProficiency();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(dataMapper.toEntity(any(LanguageProficiencyDto.class))).thenReturn(
        languageProficiency);
    when(dataMapper.toDto(any(LanguageProficiency.class))).thenReturn(
        languageProficiencyDto);
    when(userRepository.save(any(User.class))).thenReturn(user);

    LanguageProficiencyDto result = userService.createLanguageProficiency(userId,
        languageProficiencyDto);

    assertNotNull(result);
    assertEquals(languageProficiencyDto, result);
    verify(userRepository).findById(userId);
    verify(userRepository).save(user);
    verify(dataMapper).toEntity(any(LanguageProficiencyDto.class));
    verify(dataMapper).toDto(any(LanguageProficiency.class));
  }

  @Test
  void createLanguageProficiencyThrowUserNotFoundExceptionTest() {
    LanguageProficiencyDto languageProficiencyDto = LanguageProficiencyDto.builder()
        .id(1L)
        .name(ENGLISH)
        .level(INTERMEDIATE_B1)
        .build();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.createLanguageProficiency(userId, languageProficiencyDto));
  }

  @Test
  void createLanguageProficiencyThrowAlreadyExistExceptionTest() {
    LanguageProficiencyDto languageProficiencyDto = LanguageProficiencyDto.builder()
        .id(1L)
        .name(ENGLISH)
        .level(INTERMEDIATE_B1)
        .build();
    LanguageProficiency existingLanguageProficiency = LanguageProficiency.builder()
        .id(1L)
        .name(ENGLISH)
        .level(INTERMEDIATE_B1)
        .build();
    List<LanguageProficiency> languageProficiencies = new ArrayList<>();
    languageProficiencies.add(existingLanguageProficiency);
    User user = User.builder()
        .id(userId)
        .languageProficiency(languageProficiencies)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    assertThrows(LanguageProficiencyAlreadyExistException.class, () -> {
      userService.createLanguageProficiency(userId, languageProficiencyDto);
    });
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).save(user);
  }
}