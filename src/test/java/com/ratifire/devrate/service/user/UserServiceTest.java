package com.ratifire.devrate.service.user;

import static com.ratifire.devrate.enums.LanguageProficiencyLevel.INTERMEDIATE_B1;
import static com.ratifire.devrate.enums.LanguageProficiencyName.ENGLISH;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
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
  private DataMapper dataMapper;

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
    when(dataMapper.toDto(any(User.class))).thenReturn(testUserDto);

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
    when(dataMapper.toEntity(any(UserDto.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);

    User createdUser = userService.create(testUserDto);

    assertEquals(testUserDto.getId(), createdUser.getId());
    verify(userRepository).save(testUser);
    verify(dataMapper).toEntity(testUserDto);
  }

  @Test
  void whenUpdate_UserNotFound_ThrowUserNotFoundException() {
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.update(testUserDto));
  }

  @Test
  void whenUpdate_Successful_ReturnCreatedUserDto() {
    when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
    when(dataMapper.updateEntity(any(UserDto.class), any(User.class))).thenReturn(testUser);
    when(userRepository.save(any())).thenReturn(testUser);
    when(dataMapper.toDto(any(User.class))).thenReturn(testUserDto);

    UserDto updatedUserDto = userService.update(testUserDto);

    assertEquals(testUserDto.getId(), updatedUserDto.getId());
    verify(userRepository).findById(userId);
    verify(userRepository).save(testUser);
    verify(dataMapper).updateEntity(testUserDto, testUser);
    verify(dataMapper).toDto(testUser);
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